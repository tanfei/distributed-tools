package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.mapreduce.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.hadoop.LazyCounterSet
 * Counters are passed via hartebeats so I may not need this
 * User: steven
 * Date: 12/8/11
 */
public class LazyCounterSet {
    public static final LazyCounterSet[] EMPTY_ARRAY = {};

    private final TaskInputOutputContext m_Context;
    private final Map<String, LazyCounter> m_Counters = new HashMap<String, LazyCounter>();
    // every 10 sec we increment
    private static final long MAX_HOLD_TIME = 10000;

    private LazyCounterSet(final TaskInputOutputContext pContext) {
        m_Context = pContext;
    }

    public void commitCounters() {
        for (LazyCounter counter : m_Counters.values())
            counter.commit();
    }

    public void incrementCounter(String s1, String s2) {
        incrementCounter(s1, s2, 1);
    }

    public void incrementCounter(String s1, String s2, long l) {
        LazyCounter lc = getCounter(s1, s2);
        lc.increment(l);
    }

    protected LazyCounter getCounter(String s1, String s2) {
        synchronized (m_Counters) {
            String id = buildId(s1, s2);
            LazyCounter lazyCounter = m_Counters.get(id);
            if (lazyCounter == null) {
                lazyCounter = new LazyCounter(s1, s2);
                m_Counters.put(id, lazyCounter);
            }
            return lazyCounter;
        }
    }

    public static String buildId(final String s1, final String s2) {
        return s1 + ":" + s2;
    }

    public TaskInputOutputContext getContext() {
        return m_Context;
    }

    public class LazyCounter {
        private final String m_S1;
        private final String m_S2;
        private final String m_Id;
        private long m_Held;
        private long m_LastUpdate;


        public LazyCounter(final String s1, String s2) {
            m_Id = buildId(s1, s2);
            m_S1 = s1;
            m_S2 = s2;
            m_LastUpdate = System.currentTimeMillis();
        }

        public synchronized void increment(long added) {
            m_Held += added;
            long now = System.currentTimeMillis();
            if ((now - m_LastUpdate) > MAX_HOLD_TIME)
                commit();
        }

        public synchronized void commit() {
            TaskInputOutputContext context = getContext();
            Counter counter = context.getCounter(m_S1, m_S2);
            counter.increment(m_Held);
            m_LastUpdate = System.currentTimeMillis();
            m_Held = 0;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final LazyCounter that = (LazyCounter) o;

            if (m_Id != null ? !m_Id.equals(that.m_Id) : that.m_Id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return m_Id != null ? m_Id.hashCode() : 0;
        }
    }

}
