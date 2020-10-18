package com.bemonovoid.playqd.core;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

public class SampleReactiveStreams {

    public static void main(String[] args) {

        DirectProcessor<String> range = DirectProcessor.create();

        Flux<String> hotRange = range.map(String::toUpperCase);
//                .doOnError(throwable -> System.out.println("RANGE: " + throwable.getMessage()));

        hotRange.subscribe(new MySubscriber("S1"));


        range.onNext("first");
        range.onNext("second");


        MySubscriber mySubscriber2 = new MySubscriber("S2");
        hotRange.subscribe(mySubscriber2);

        range.onNext("three");

//        range.onError(new IllegalArgumentException("Manual error"));

//        mySubscriber2.dispose();

        range.onNext("four");

        range.onComplete();



    }

    public static class MySubscriber extends BaseSubscriber<String> {
        private final String id;


        public MySubscriber(String id) {
            this.id = id;
        }
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            super.hookOnSubscribe(subscription);
        }

        @Override
        protected void hookOnNext(String value) {
            if (value.equals("SECOND")) {
                dispose();
//                throw new IllegalArgumentException("WE FAILED");
            } else {
                System.out.println(id + ": " + value);

            }
        }

        @Override
        protected void hookOnComplete() {
            System.out.println(id + ": Completed");;
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            System.out.println(id + ": Error = " + throwable.getMessage());
        }

        @Override
        protected void hookOnCancel() {
            System.out.println(id + ": Cancelled");
        }

        @Override
        protected void hookFinally(SignalType type) {
            System.out.println(id + ": signal = " + type );
        }
    }
}
