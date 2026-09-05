package org.example.Analysis;

public interface Insight {

    Severity severity();
    String title();
    InsightType type();

    enum Severity{
        Error,
        Warning,
        Info
    }


}
