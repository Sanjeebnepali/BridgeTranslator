package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \u00172\u00020\u0001:\u0002\u0017\u0018B\u0005\u00a2\u0006\u0002\u0010\u0002J&\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0016J\u001a\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\f2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0016J\u001a\u0010\u0015\u001a\u00020\b2\u0012\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0005\u001a\u0010\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/example/bridgetranslator/LanguageBottomSheet;", "Lcom/google/android/material/bottomsheet/BottomSheetDialogFragment;", "()V", "languageManager", "Lcom/example/bridgetranslator/LanguageManager;", "onLanguageSelected", "Lkotlin/Function1;", "Lcom/example/bridgetranslator/Language;", "", "recyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onViewCreated", "view", "setOnLanguageSelectedListener", "listener", "Companion", "LanguageAdapter", "app_debug"})
public final class LanguageBottomSheet extends com.google.android.material.bottomsheet.BottomSheetDialogFragment {
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private com.example.bridgetranslator.LanguageManager languageManager;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function1<? super com.example.bridgetranslator.Language, kotlin.Unit> onLanguageSelected;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ARG_IS_SOURCE = "is_source";
    @org.jetbrains.annotations.NotNull()
    public static final com.example.bridgetranslator.LanguageBottomSheet.Companion Companion = null;
    
    public LanguageBottomSheet() {
        super();
    }
    
    public final void setOnLanguageSelectedListener(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.example.bridgetranslator.Language, kotlin.Unit> listener) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/example/bridgetranslator/LanguageBottomSheet$Companion;", "", "()V", "ARG_IS_SOURCE", "", "newInstance", "Lcom/example/bridgetranslator/LanguageBottomSheet;", "isSource", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.bridgetranslator.LanguageBottomSheet newInstance(boolean isSource) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0082\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0016B/\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\u0002\u0010\fJ\b\u0010\r\u001a\u00020\u000eH\u0016J \u0010\u000f\u001a\u00020\u000b2\u000e\u0010\u0010\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u000eH\u0016J \u0010\u0012\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u000eH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/example/bridgetranslator/LanguageBottomSheet$LanguageAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/bridgetranslator/LanguageBottomSheet$LanguageAdapter$ViewHolder;", "Lcom/example/bridgetranslator/LanguageBottomSheet;", "languages", "", "Lcom/example/bridgetranslator/Language;", "selectedCode", "", "onItemSelected", "Lkotlin/Function1;", "", "(Lcom/example/bridgetranslator/LanguageBottomSheet;Ljava/util/List;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "ViewHolder", "app_debug"})
    final class LanguageAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.bridgetranslator.LanguageBottomSheet.LanguageAdapter.ViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.example.bridgetranslator.Language> languages = null;
        @org.jetbrains.annotations.NotNull()
        private java.lang.String selectedCode;
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<com.example.bridgetranslator.Language, kotlin.Unit> onItemSelected = null;
        
        public LanguageAdapter(@org.jetbrains.annotations.NotNull()
        java.util.List<com.example.bridgetranslator.Language> languages, @org.jetbrains.annotations.NotNull()
        java.lang.String selectedCode, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super com.example.bridgetranslator.Language, kotlin.Unit> onItemSelected) {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.example.bridgetranslator.LanguageBottomSheet.LanguageAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.example.bridgetranslator.LanguageBottomSheet.LanguageAdapter.ViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\f\u00a8\u0006\u000f"}, d2 = {"Lcom/example/bridgetranslator/LanguageBottomSheet$LanguageAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Lcom/example/bridgetranslator/LanguageBottomSheet$LanguageAdapter;Landroid/view/View;)V", "ivCheck", "Landroid/widget/ImageView;", "getIvCheck", "()Landroid/widget/ImageView;", "tvFlag", "Landroid/widget/TextView;", "getTvFlag", "()Landroid/widget/TextView;", "tvName", "getTvName", "app_debug"})
        public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView tvFlag = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView tvName = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.ImageView ivCheck = null;
            
            public ViewHolder(@org.jetbrains.annotations.NotNull()
            android.view.View view) {
                super(null);
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.TextView getTvFlag() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.TextView getTvName() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.ImageView getIvCheck() {
                return null;
            }
        }
    }
}