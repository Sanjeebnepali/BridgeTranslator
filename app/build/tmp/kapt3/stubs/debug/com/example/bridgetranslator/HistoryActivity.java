package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\u001c\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u00062\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0002J\u0012\u0010\u001a\u001a\u00020\u00162\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0014J\b\u0010\u001d\u001a\u00020\u0016H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"Lcom/example/bridgetranslator/HistoryActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "adapter", "Lcom/example/bridgetranslator/HistoryAdapter;", "allItems", "", "Lcom/example/bridgetranslator/HistoryEntity;", "dao", "Lcom/example/bridgetranslator/HistoryDao;", "emptyState", "Landroid/view/View;", "filterAll", "Landroid/widget/TextView;", "filterStarred", "rvHistory", "Landroidx/recyclerview/widget/RecyclerView;", "searchQuery", "", "showStarredOnly", "", "applyFilter", "", "buildGroupedList", "Lcom/example/bridgetranslator/ListItem;", "entries", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "updateFilterTabs", "app_debug"})
public final class HistoryActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.bridgetranslator.HistoryDao dao;
    private com.example.bridgetranslator.HistoryAdapter adapter;
    private androidx.recyclerview.widget.RecyclerView rvHistory;
    private android.view.View emptyState;
    private android.widget.TextView filterAll;
    private android.widget.TextView filterStarred;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.example.bridgetranslator.HistoryEntity> allItems;
    private boolean showStarredOnly = false;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String searchQuery = "";
    
    public HistoryActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void applyFilter() {
    }
    
    private final void updateFilterTabs() {
    }
    
    private final java.util.List<com.example.bridgetranslator.ListItem> buildGroupedList(java.util.List<com.example.bridgetranslator.HistoryEntity> entries) {
        return null;
    }
}