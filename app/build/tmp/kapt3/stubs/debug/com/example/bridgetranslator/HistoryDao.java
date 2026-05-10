package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u000b0\nH\'J\u0016\u0010\f\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u001e\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0012\u00a8\u0006\u0013"}, d2 = {"Lcom/example/bridgetranslator/HistoryDao;", "", "clearAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "entry", "Lcom/example/bridgetranslator/HistoryEntity;", "(Lcom/example/bridgetranslator/HistoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllFlow", "Lkotlinx/coroutines/flow/Flow;", "", "insert", "setStarred", "id", "", "starred", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface HistoryDao {
    
    @androidx.room.Query(value = "SELECT * FROM history ORDER BY timestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.bridgetranslator.HistoryEntity>> getAllFlow();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.example.bridgetranslator.HistoryEntity entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.example.bridgetranslator.HistoryEntity entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE history SET isStarred = :starred WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setStarred(long id, boolean starred, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM history")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}