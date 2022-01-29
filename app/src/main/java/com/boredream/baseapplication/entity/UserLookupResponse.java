package com.boredream.baseapplication.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserLookupResponse implements Serializable {

    private ArrayList<FirebaseUser> users;

    public static class FirebaseUser implements Serializable {

        private String localId;
        private String email;
        private List<ProviderUserInfoBean> providerUserInfo;

        public String getLocalId() {
            return localId;
        }

        public void setLocalId(String localId) {
            this.localId = localId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<ProviderUserInfoBean> getProviderUserInfo() {
            return providerUserInfo;
        }

        public void setProviderUserInfo(List<ProviderUserInfoBean> providerUserInfo) {
            this.providerUserInfo = providerUserInfo;
        }
    }

    public static class ProviderUserInfoBean implements Serializable {
        private String email;
        private String rawId;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRawId() {
            return rawId;
        }

        public void setRawId(String rawId) {
            this.rawId = rawId;
        }
    }

    public ArrayList<FirebaseUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<FirebaseUser> users) {
        this.users = users;
    }
}
