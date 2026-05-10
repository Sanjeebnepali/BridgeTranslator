package com.bridge.translator.camera.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B9\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0003\u00a2\u0006\u0002\u0010\rJ\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001a\u001a\u00020\nH\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\f0\u0003H\u00c6\u0003JG\u0010\u001c\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0003H\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010 \u001a\u00020!H\u00d6\u0001J\t\u0010\"\u001a\u00020#H\u00d6\u0001R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006$"}, d2 = {"Lcom/bridge/translator/camera/data/OrchestrationResult;", "", "banners", "", "Lcom/bridge/translator/camera/data/TranslationBanner;", "quality", "Lcom/bridge/translator/camera/data/QualityReport;", "distance", "Lcom/bridge/translator/camera/data/DistanceReport;", "processingMs", "", "allBlocks", "Lcom/bridge/translator/camera/data/TextOrientationBlock;", "(Ljava/util/List;Lcom/bridge/translator/camera/data/QualityReport;Lcom/bridge/translator/camera/data/DistanceReport;JLjava/util/List;)V", "getAllBlocks", "()Ljava/util/List;", "getBanners", "getDistance", "()Lcom/bridge/translator/camera/data/DistanceReport;", "getProcessingMs", "()J", "getQuality", "()Lcom/bridge/translator/camera/data/QualityReport;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
public final class OrchestrationResult {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bridge.translator.camera.data.TranslationBanner> banners = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.camera.data.QualityReport quality = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.camera.data.DistanceReport distance = null;
    private final long processingMs = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bridge.translator.camera.data.TextOrientationBlock> allBlocks = null;
    
    public OrchestrationResult(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bridge.translator.camera.data.TranslationBanner> banners, @org.jetbrains.annotations.NotNull()
    com.bridge.translator.camera.data.QualityReport quality, @org.jetbrains.annotations.NotNull()
    com.bridge.translator.camera.data.DistanceReport distance, long processingMs, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bridge.translator.camera.data.TextOrientationBlock> allBlocks) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bridge.translator.camera.data.TranslationBanner> getBanners() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.camera.data.QualityReport getQuality() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.camera.data.DistanceReport getDistance() {
        return null;
    }
    
    public final long getProcessingMs() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bridge.translator.camera.data.TextOrientationBlock> getAllBlocks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bridge.translator.camera.data.TranslationBanner> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.camera.data.QualityReport component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.camera.data.DistanceReport component3() {
        return null;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bridge.translator.camera.data.TextOrientationBlock> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.camera.data.OrchestrationResult copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bridge.translator.camera.data.TranslationBanner> banners, @org.jetbrains.annotations.NotNull()
    com.bridge.translator.camera.data.QualityReport quality, @org.jetbrains.annotations.NotNull()
    com.bridge.translator.camera.data.DistanceReport distance, long processingMs, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bridge.translator.camera.data.TextOrientationBlock> allBlocks) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}