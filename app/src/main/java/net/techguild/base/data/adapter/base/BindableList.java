package net.techguild.base.data.adapter.base;

//Bindable list interface used in all UI Lists that are connected to a custom adapter
public interface BindableList {
    // Called when initial data load begins. Also used when pull to refresh is called
    void onRefreshBegin();

    // Called when initial data load ends
    void onRefreshEnd();

    void onShowMoreBegin();

    void onShowMoreEnd();

    void onError();
}
