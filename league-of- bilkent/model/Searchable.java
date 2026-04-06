package model;


public interface Searchable {

    boolean matchesSearch(String query);//whether the object is matching with user's search query

    String getSearchSummary();
}
