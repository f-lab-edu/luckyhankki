package com.java.luckyhankki.domain.store;

public interface StoreProjection {
    Long getId();
    String getName();
    String getPhone();
    String getAddress();
    boolean getIsApproved();
    int getReportCount();
}
