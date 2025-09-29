package ru.dbudyak.entangler;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Created by dbudyak on 28.05.2014.
 */
public class Data {

    private RealMatrix channel1;
    private RealMatrix channel2;

    private boolean channel1isUsed = false;
    private boolean channel2isUsed = false;

    private Data(DataBuilder builder) {
        this.channel1 = builder.channel1;
        this.channel2 = builder.channel2;
    }

    public boolean isEmptyChannel1() {
        return channel1 == null;
    }

    public boolean isEmptyChannel2() {
        return channel2 == null;
    }

    public void setChannel1Used() {
        this.channel1isUsed = true;
    }

    public void setChannel2isUsed() {
        this.channel2isUsed = true;
    }

    public boolean isUsedChannel1() {
        return this.channel1isUsed;
    }

    public boolean isUsedChannel2() {
        return this.channel2isUsed;
    }

    public RealMatrix getChannel1() {
        return channel1;
    }

    public void setChannel1(RealMatrix channel1) {
        this.channel1 = channel1;
    }

    public RealMatrix getChannel2() {
        return channel2;
    }

    public void setChannel2(RealMatrix channel2) {
        this.channel2 = channel2;
    }

    public static class DataBuilder {
        private RealMatrix channel1;
        private RealMatrix channel2;

        public DataBuilder channel1(RealMatrix channel1) {
            this.channel1 = channel1;
            return this;
        }

        public DataBuilder channel2(RealMatrix channel2) {
            this.channel2 = channel2;
            return this;
        }

        public Data build() {
            return new Data(this);
        }
    }
}
