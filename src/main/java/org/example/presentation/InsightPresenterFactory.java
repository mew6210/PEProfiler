package org.example.presentation;

public class InsightPresenterFactory {

    public static InsightPresenter create(InsightPresenterType type){
        return switch(type){
            case MarkDown -> new MarkDownPresenter();
        };
    }

}
