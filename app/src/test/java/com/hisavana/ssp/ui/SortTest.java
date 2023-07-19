package com.hisavana.ssp.ui;

import com.hisavana.common.bean.AdCache;
import com.hisavana.common.interfacz.ICacheAd;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author frank
 * @date 2020/7/29
 * E-Mail Address：qi.xia@transsion.com
 */
public class SortTest {
    private AdCache<TestInfo> caches = new AdCache<>();
    private ArrayList<TestInfo> infos = new ArrayList<>();
    private String slotId = "3423423243";

    @Before
    public void mock() {
        TestInfo testInfo0 = new TestInfo();
        TestInfo testInfo1 = new TestInfo();
        TestInfo testInfo2 = new TestInfo();
        TestInfo testInfo3 = new TestInfo();
        testInfo0.setExpired(true);
        testInfo0.setPriority(1);
        testInfo0.setAdType(1);
        testInfo0.setValidTimeLinit(1000L);

        testInfo1.setExpired(false);
        testInfo1.setPriority(1);
        testInfo1.setAdType(2);
        testInfo1.setValidTimeLinit(1000L);

        testInfo2.setExpired(true);
        testInfo2.setPriority(2);
        testInfo2.setAdType(3);
        testInfo2.setValidTimeLinit(1000L);

        testInfo3.setExpired(true);
        testInfo3.setPriority(1);
        testInfo3.setAdType(1);
        testInfo3.setValidTimeLinit(1000L);


        infos.add(testInfo0);
        infos.add(testInfo1);
        infos.add(testInfo2);
        infos.add(testInfo3);
    }

    @Test
    public void testCacheComparator() {
        /**
         * 测试异常要超过32个
         */
        caches.addCaches(slotId, infos);
        caches.addCaches(slotId, infos);
        caches.addCaches(slotId, infos);
        caches.addCaches(slotId, infos);
        caches.addCaches(slotId, infos);
        caches.addCaches(slotId, infos);
        caches.addCaches(slotId, infos);
        caches.addCaches(slotId, infos);
        caches.addCaches(slotId, infos);
        caches.addCaches(slotId, infos);
        ArrayList<TestInfo> caches = this.caches.getCaches(slotId, 3,false);
    }

    class TestInfo implements ICacheAd {
        boolean expired;
        int priority;

        public void setExpired(boolean expired) {
            this.expired = expired;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public void setValidTimeLinit(long validTimeLinit) {
            this.validTimeLinit = validTimeLinit;
        }

        public void setAdType(int adType) {
            this.adType = adType;
        }

        long validTimeLinit;
        int adType;

        @Override
        public boolean isExpired() {
            return expired;
        }

        @Override
        public int getAdSource() {
            return 0;
        }

        @Override
        public int getAdType() {
            return adType;
        }

        @Override
        public void setAdSource(int source) {

        }

        @Override
        public long getValidTimeLimit() {
            return validTimeLinit;
        }

        @Override
        public String getPlacementId() {
            return null;
        }


        @Override
        public void destroyAd() {

        }

        @Override
        public double getEcpmPrice() {
            return 0;
        }

        @Override
        public void setEcpmPrice(double price) {

        }

        @Override
        public void setSecondPrice(double v) {

        }

        @Override
        public void setRequestType(int i) {

        }

        @Override
        public boolean isAdxAd() {
            return false;
        }

        @Override
        public boolean isEwAd() {
            return false;
        }

        @Override
        public void setTimeOut(boolean b) {

        }

        @Override
        public void detachContext() {

        }

        @Override
        public String getAdSeatType() {
            return null;
        }

        @Override
        public String getFilterSource() {
            return null;
        }

        @Override
        public boolean isOfflineAd() {
            return false;
        }

        @Override
        public void setMaxPrice(double v) {

        }
    }
}
