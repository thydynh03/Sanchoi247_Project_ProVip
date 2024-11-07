    package com.example.SanChoi247.model.entity;

    import java.time.LocalDateTime;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public class Booking {
        // booking_id int auto_increment primary key,
        // date timestamp,
        // san_id int,
        // foreign key (san_id) references san(san_id),
        // total int,
        // status tinyint,
        // vnpay_data json
        private int booking_id;
        private LocalDateTime date;
        private User user;
        private San san;
        private ScheduleBooking scheduleBooking;
        private double totalprice;
        private PaymentStatus status;
        private String vnpayData;

        public boolean isPendingRefund() {
            return status == PaymentStatus.PENDING_PARTIAL_REFUND || status == PaymentStatus.PENDING_TOTAL_REFUND;
        }

        public boolean isRefunded() {
            return status == PaymentStatus.TOTALLY_REFUNDED || status == PaymentStatus.PARTIALLY_REFUNDED;
        }

        public enum PaymentStatus {
            SUCCESS(0),
            FAILED(1),
            PENDING(2),
            TOTALLY_REFUNDED(3),
            PARTIALLY_REFUNDED(4),
            PENDING_TOTAL_REFUND(5),
            PENDING_PARTIAL_REFUND(6);

            private final int value;

            PaymentStatus(int value) {
                this.value = value;
            }

            public int toInteger() {
                return value;
            }

            public static PaymentStatus fromInteger(int value) {
                for (PaymentStatus status : PaymentStatus.values()) {
                    if (status.value == value) {
                        return status;
                    }
                }
                return null;
            }
        }
    }
