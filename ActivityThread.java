package com.rex.medium;

public class ActivityThread {


    @Override
    public void handleStopActivity(IBinder token, boolean show, int configChanges,
                                   PendingTransactionActions pendingActions, boolean finalStateRequest, String reason) {
        final ActivityClientRecord r = mActivities.get(token);
        r.activity.mConfigChangeFlags |= configChanges;

        final StopInfo stopInfo = new StopInfo();
        performStopActivityInner(r, stopInfo, show, true /* saveState */, finalStateRequest,
                reason);

        // ...ignore unnecessary code

        stopInfo.setActivity(r);
        stopInfo.setState(r.state);
        stopInfo.setPersistentState(r.persistentState);
        pendingActions.setStopInfo(stopInfo);
        mSomeActivitiesChanged = true;
    }


    /**
     * Calls {@link Activity#onStop()} and {@link Activity#onSaveInstanceState(Bundle)}, and updates
     * the client record's state.
     * All calls to stop an activity must be done through this method to make sure that
     * {@link Activity#onSaveInstanceState(Bundle)} is also executed in the same call.
     */
    private void callActivityOnStop(ActivityClientRecord r, boolean saveState, String reason) {
        final boolean shouldSaveState = saveState && !r.activity.mFinished && r.state == null
                && !r.isPreHoneycomb();

        // ...ignore unnecessary code

        try {
            r.activity.performStop(false /*preserveWindow*/, reason); // onStop() will be invoked
        } catch (SuperNotCalledException e) {
            throw e;
        } catch (Exception e) {
            if (!mInstrumentation.onException(r.activity, e)) {
                throw new RuntimeException(
                        "Unable to stop activity "
                                + r.intent.getComponent().toShortString()
                                + ": " + e.toString(), e);
            }
        }

        if (shouldSaveState) {
            callActivityOnSaveInstanceState(r);
        }
    }

    private void callActivityOnSaveInstanceState(ActivityClientRecord r) {
        r.state = new Bundle();
        r.state.setAllowFds(false);

        // ...ignore unnecessary code
        mInstrumentation.callActivityOnSaveInstanceState(r.activity, r.state); // onSaveInstanceState() will be invoked
    }
}