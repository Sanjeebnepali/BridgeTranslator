package com.bridge.translator.camera.cache;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\"\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0011\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u0012\u00a8\u0006\u0013"}, d2 = {"Lcom/bridge/translator/camera/cache/TranslationCacheDao;", "", "clearAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "count", "", "get", "Lcom/bridge/translator/camera/cache/TranslationCacheEntity;", "key", "", "now", "", "(Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "purgeExpired", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "put", "entry", "(Lcom/bridge/translator/camera/cache/TranslationCacheEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface TranslationCacheDao {
    
    @androidx.room.Query(value = "SELECT * FROM translation_cache WHERE cacheKey = :key AND expiresAt > :now LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object get(@org.jetbrains.annotations.NotNull()
    java.lang.String key, long now, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.bridge.translator.camera.cache.TranslationCacheEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object put(@org.jetbrains.annotations.NotNull()
    com.bridge.translator.camera.cache.TranslationCacheEntity entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Delete expired entries — call periodically to keep the DB small.
     */
    @androidx.room.Query(value = "DELETE FROM translation_cache WHERE expiresAt <= :now")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object purgeExpired(long now, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM translation_cache")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM translation_cache")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object count(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}