    package com.example.abcscialchat;

    public class msgModelClass {
        private String message;     // Nội dung tin nhắn hoặc URL ảnh
        private String senderId;    // ID của người gửi
        private long timeStamp;     // Thời gian gửi tin nhắn
        private boolean isImage;    // Đánh dấu đây là tin nhắn hình ảnh hay văn bản

        // Constructor mặc định
        public msgModelClass() {
        }

        // Constructor cho tin nhắn văn bản
        public msgModelClass(String message, String senderId, long timeStamp) {
            this.message = message;
            this.senderId = senderId;
            this.timeStamp = timeStamp;
            this.isImage = false;   // Mặc định là văn bản
        }

        // Constructor cho tin nhắn hình ảnh
        public msgModelClass(String message, String senderId, long timeStamp, boolean isImage) {
            this.message = message;
            this.senderId = senderId;
            this.timeStamp = timeStamp;
            this.isImage = isImage;
        }

        // Getter và Setter
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public boolean isImage() {
            return isImage;
        }

        public void setImage(boolean image) {
            isImage = image;
        }
    }
