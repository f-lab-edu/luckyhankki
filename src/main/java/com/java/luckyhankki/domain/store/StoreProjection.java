package com.java.luckyhankki.domain.store;

public interface StoreProjection {
    String getName();
    String getPhone();
    String getAddress();
    boolean getIsApproved();
    int getReportCount();
}
