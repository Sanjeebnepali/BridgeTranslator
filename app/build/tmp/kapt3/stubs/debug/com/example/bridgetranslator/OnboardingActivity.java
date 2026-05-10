package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\u0014B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000b\u001a\u00020\fH\u0002J\u0012\u0010\r\u001a\u00020\f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0014J\b\u0010\u0010\u001a\u00020\fH\u0002J\u0010\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u0013H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/example/bridgetranslator/OnboardingActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "btnNext", "Landroid/view/View;", "prefs", "Landroid/content/SharedPreferences;", "tvNextButton", "Landroid/widget/TextView;", "viewPager", "Landroidx/viewpager2/widget/ViewPager2;", "completeOnboarding", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "startHome", "updateUI", "position", "", "OnboardingAdapter", "app_debug"})
public final class OnboardingActivity extends androidx.appcompat.app.AppCompatActivity {
    private androidx.viewpager2.widget.ViewPager2 viewPager;
    private android.view.View btnNext;
    private android.widget.TextView tvNextButton;
    private android.content.SharedPreferences prefs;
    
    public OnboardingActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void updateUI(int position) {
    }
    
    private final void completeOnboarding() {
    }
    
    private final void startHome() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0014B\u0005\u00a2\u0006\u0002\u0010\u0004J\b\u0010\n\u001a\u00020\u000bH\u0016J \u0010\f\u001a\u00020\r2\u000e\u0010\u000e\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u000bH\u0016J \u0010\u0010\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000bH\u0016R\u0016\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\bR\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\b\u00a8\u0006\u0015"}, d2 = {"Lcom/example/bridgetranslator/OnboardingActivity$OnboardingAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/bridgetranslator/OnboardingActivity$OnboardingAdapter$OnboardingViewHolder;", "Lcom/example/bridgetranslator/OnboardingActivity;", "(Lcom/example/bridgetranslator/OnboardingActivity;)V", "descriptions", "", "", "[Ljava/lang/String;", "titles", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "OnboardingViewHolder", "app_debug"})
    public final class OnboardingAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.bridgetranslator.OnboardingActivity.OnboardingAdapter.OnboardingViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String[] titles = {"Step 1: Enable\nBridge.", "Step 2: Aim & \nTranslate.", "Step 3: Global Chat"};
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String[] descriptions = {"Allow Bridge to float over other apps for instant translation.", "Point your camera at any text or use screen selection to bridge the language gap.", "Type in your language; we\'ll send it in theirs. Seamless communication across any border."};
        
        public OnboardingAdapter() {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.example.bridgetranslator.OnboardingActivity.OnboardingAdapter.OnboardingViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.example.bridgetranslator.OnboardingActivity.OnboardingAdapter.OnboardingViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000bR\u0011\u0010\u0010\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\b\u00a8\u0006\u0012"}, d2 = {"Lcom/example/bridgetranslator/OnboardingActivity$OnboardingAdapter$OnboardingViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Lcom/example/bridgetranslator/OnboardingActivity$OnboardingAdapter;Landroid/view/View;)V", "desc", "Landroid/widget/TextView;", "getDesc", "()Landroid/widget/TextView;", "graphic1", "getGraphic1", "()Landroid/view/View;", "graphic2", "getGraphic2", "graphic3", "getGraphic3", "title", "getTitle", "app_debug"})
        public final class OnboardingViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView title = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView desc = null;
            @org.jetbrains.annotations.NotNull()
            private final android.view.View graphic1 = null;
            @org.jetbrains.annotations.NotNull()
            private final android.view.View graphic2 = null;
            @org.jetbrains.annotations.NotNull()
            private final android.view.View graphic3 = null;
            
            public OnboardingViewHolder(@org.jetbrains.annotations.NotNull()
            android.view.View view) {
                super(null);
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.TextView getTitle() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.TextView getDesc() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.view.View getGraphic1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.view.View getGraphic2() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.view.View getGraphic3() {
                return null;
            }
        }
    }
}