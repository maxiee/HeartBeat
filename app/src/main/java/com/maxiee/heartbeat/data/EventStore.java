package com.maxiee.heartbeat.data;

import android.content.Context;
import android.database.Cursor;

import com.google.android.agera.Function;
import com.google.android.agera.Merger;
import com.google.android.agera.Observable;
import com.google.android.agera.Receiver;
import com.google.android.agera.Repository;
import com.google.android.agera.Reservoir;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;
import com.google.android.agera.database.SqlDeleteRequest;
import com.google.android.agera.database.SqlInsertRequest;
import com.google.android.agera.database.SqlRequest;
import com.google.android.agera.database.SqlUpdateRequest;
import com.maxiee.heartbeat.database.HBSqlDatabaseSupplier;
import com.maxiee.heartbeat.database.utils.EventsDBUtils;
import com.maxiee.heartbeat.model.Event;

import java.util.List;
import java.util.concurrent.Executor;

import static com.google.android.agera.Functions.staticFunction;
import static com.google.android.agera.Mergers.staticMerger;
import static com.google.android.agera.Repositories.repositoryWithInitialValue;
import static com.google.android.agera.RepositoryConfig.SEND_INTERRUPT;
import static com.google.android.agera.Reservoirs.reservoir;
import static com.google.android.agera.Result.failure;
import static com.google.android.agera.Suppliers.staticSupplier;
import static com.google.android.agera.database.SqlDatabaseFunctions.databaseDeleteFunction;
import static com.google.android.agera.database.SqlDatabaseFunctions.databaseInsertFunction;
import static com.google.android.agera.database.SqlDatabaseFunctions.databaseQueryFunction;
import static com.google.android.agera.database.SqlDatabaseFunctions.databaseUpdateFunction;
import static com.google.android.agera.database.SqlRequests.sqlRequest;
import static com.maxiee.heartbeat.database.HBSqlDatabaseSupplier.databaseSupplier;
import static java.util.Collections.emptyList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Created by maxiee on 16/4/23.
 */
public class EventStore {
    private static final List<Event> INITIAL_VALUE = emptyList();


    private static EventStore mEventStore;

    private final Receiver<Object> writeRequestReceiver;
    private final Repository<List<Event>> eventRepository;

    private EventStore(
            final Repository<List<Event>> eventRepository,
            final Receiver<Object> writeRequestReceiver) {
        this.eventRepository = eventRepository;
        this.writeRequestReceiver = writeRequestReceiver;
    }

    public synchronized static EventStore eventStore(final Context context) {
        if (mEventStore != null) return mEventStore;
        final Executor executor = newSingleThreadExecutor();
        final HBSqlDatabaseSupplier databaseSupplier = databaseSupplier(context);

        final Function<SqlInsertRequest, Result<Long>>
                insertNoteFunction = databaseInsertFunction(databaseSupplier);

        final Function<SqlUpdateRequest, Result<Integer>>
                updateNoteFunction = databaseUpdateFunction(databaseSupplier);

        final Function<SqlDeleteRequest, Result<Integer>>
                deleteNoteFunction = databaseDeleteFunction(databaseSupplier);

        final Function<Object, Result<? extends Number>> writeOperation =
                input -> {
                    if (input instanceof SqlInsertRequest) {
                        return insertNoteFunction.apply((SqlInsertRequest) input);
                    }
                    if (input instanceof SqlUpdateRequest) {
                        return updateNoteFunction.apply((SqlUpdateRequest) input);
                    }
                    if (input instanceof SqlDeleteRequest) {
                        return deleteNoteFunction.apply((SqlDeleteRequest) input);
                    }
                    return failure();
                };

        final Reservoir<Object> writeRequestReservoir = reservoir();

        final Number unimportantValue = 0;
        final Merger<Number, Number, Boolean> alwaysNotify = staticMerger(true);
        final Observable writeReaction = repositoryWithInitialValue(unimportantValue)
                .observe(writeRequestReservoir)
                .onUpdatesPerLoop()
                .goTo(executor)
                .attemptGetFrom(writeRequestReservoir).orSkip()
                .thenAttemptTransform(writeOperation).orSkip()
                .notifyIf(alwaysNotify)
                .compile();

        // Keep the reacting repository in this lazy singleton activated for the full app life cycle.
        // This is optional -- it allows the write requests submitted when the notes repository is not
        // active to still be processed asap.
        final Updatable dummyUpdatable = () -> {};
        writeReaction.addUpdatable(dummyUpdatable);

        Function<Cursor, Event> cursorToEvent = EventsDBUtils.cursorToEvent();

        final Supplier<SqlRequest> getEventQuerySupplier =
                staticSupplier(sqlRequest().sql(EventsDBUtils.SQL_GET_ALL).compile());
        final Repository<List<Event>> eventRepository = repositoryWithInitialValue(INITIAL_VALUE)
                .observe(writeReaction)
                .onUpdatesPerLoop()
                .goTo(executor)
                .getFrom(getEventQuerySupplier)
                .thenAttemptTransform(databaseQueryFunction(databaseSupplier, cursorToEvent))
                .orEnd(staticFunction(INITIAL_VALUE))
                .onConcurrentUpdate(SEND_INTERRUPT)
                .onDeactivation(SEND_INTERRUPT)
                .compile();

        mEventStore = new EventStore(eventRepository, writeRequestReservoir);
        return mEventStore;
    }

    public Repository<List<Event>> getEventRepository() {
        return eventRepository;
    }

}
