package com.xiu.hystrix.rxjava;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 连载小说
 */

public class Noval {

    private Observable noval;

    public Noval(){
        noval = Observable.create(new ObservableOnSubscribe<String>(){

            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                observableEmitter.onNext("连载1");
                observableEmitter.onNext("连载2");
                observableEmitter.onNext("连载3");
                observableEmitter.onNext("连载4");
                observableEmitter.onComplete();
            }
        });

    }


    public static void main(String[] args){
        Reader reader = new Reader();

        Noval noval = new Noval();

        noval.noval.subscribe(reader.getReader());


    }


}
