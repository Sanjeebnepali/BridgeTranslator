package com.bridge.translator.camera.cache;

/**
 * Room entity that stores a single translated text entry on disk.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BK\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\b\b\u0002\u0010\n\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003JY\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010#\u001a\u00020$H\u00d6\u0001J\t\u0010%\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000eR\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000e\u00a8\u0006&"}, d2 = {"Lcom/bridge/translator/camera/cache/TranslationCacheEntity;", "", "id", "", "cacheKey", "", "sourceText", "translatedText", "sourceLang", "targetLang", "cachedAt", "expiresAt", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JJ)V", "getCacheKey", "()Ljava/lang/String;", "getCachedAt", "()J", "getExpiresAt", "getId", "getSourceLang", "getSourceText", "getTargetLang", "getTranslatedText", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
@androidx.room.Entity(tableName = "translation_cache", indices = {@androidx.room.Index(value = {"cacheKey"}, unique = true)})
public final class TranslationCacheEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    
    /**
     * Format: "sourceText|sourceLang|targetLang" (SHA-256 truncated to 64 chars)
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String cacheKey = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String sourceText = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String translatedText = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String sourceLang = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String targetLang = null;
    private final long cachedAt = 0L;
    
    /**
     * Expires after 30 days (30 * 24 * 60 * 60 * 1000 ms)
     */
    private final long expiresAt = 0L;
    
    public TranslationCacheEntity(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String cacheKey, @org.jetbrains.annotations.NotNull()
    java.lang.String sourceText, @org.jetbrains.annotations.NotNull()
    java.lang.String translatedText, @org.jetbrains.annotations.NotNull()
    java.lang.String sourceLang, @org.jetbrains.annotations.NotNull()
    java.lang.String targetLang, long cachedAt, long expiresAt) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    /**
     * Format: "sourceText|sourceLang|targetLang" (SHA-256 truncated to 64 chars)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCacheKey() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSourceText() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTranslatedText() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSourceLang() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTargetLang() {
        return null;
    }
    
    public final long getCachedAt() {
        return 0L;
    }
    
    /**
     * Expires after 30 days (30 * 24 * 60 * 60 * 1000 ms)
     */
    public final long getExpiresAt() {
        return 0L;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    public final long component7() {
        return 0L;
    }
    
    public final long component8() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.camera.cache.TranslationCacheEntity copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String cacheKey, @org.jetbrains.annotations.NotNull()
    java.lang.String sourceText, @org.jetbrains.annotations.NotNull()
    java.lang.String translatedText, @org.jetbrains.annotations.NotNull()
    java.lang.String sourceLang, @org.jetbrains.annotations.NotNull()
    java.lang.String targetLang, long cachedAt, long expiresAt) {
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