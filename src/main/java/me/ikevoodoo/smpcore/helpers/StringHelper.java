package me.ikevoodoo.smpcore.helpers;

public class StringHelper {
    private String message;
    private String prefix;
    private String postfix;

    private StringHelper(String message, String prefix, String postfix) {
        this.message = message;
        this.prefix = prefix;
        this.postfix = postfix;
    }

    public static StringHelper from(String message) {
        return new StringHelper(message,"","");
    }

    public StringHelper prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public StringHelper postfix(String postfix) {
        this.postfix = postfix;
        return this;
    }

    public StringHelper middle(int maxLen) {
        return this.middle(maxLen, 0);
    }

    public StringHelper middle(int maxLen, int ignore) {
        int total = this.calculateLength(ignore);
        if (maxLen <= 0 || maxLen < this.message.length()) {
            return this;
        }

        while (total > maxLen) {
            if (this.prefix.length() > 0)
                this.prefix(this.prefix.substring(0, this.prefix.length() - 1));

            if (this.postfix.length() > 0)
                this.postfix(this.postfix.substring(0, this.postfix.length() - 1));

            total = this.calculateLength(ignore);
        }
        return this;
    }

    @Override
    public String toString() {
        return this.prefix + this.message + this.postfix;
    }

    private int calculateLength(int ignored) {
        return this.message.length() - ignored + this.prefix.length() + this.postfix.length();
    }



}
