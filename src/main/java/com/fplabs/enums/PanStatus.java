package com.fplabs.enums;

        public enum PanStatus {
            ACTIVE("Active"),
        INACTIVE("Inactive");

        private final String value;

        PanStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
}
