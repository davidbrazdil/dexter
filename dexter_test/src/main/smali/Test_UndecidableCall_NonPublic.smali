.class public LTest_UndecidableCall_NonPublic;
.super LPropagationTest;



# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LPropagationTest;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "Undecidable call: non-public method"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "two implementations of Binder.onTransact"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 16

    # create external object
    new-instance v2, Landroid/os/Binder;
    invoke-direct {v2}, Landroid/os/Binder;-><init>()V

    # create internal object
    new-instance v3, LMyClass_BinderChild;
    invoke-direct {v3}, LMyClass_BinderChild;-><init>()V

    # "randomly" swap them
    const/4 v0, 7
    if-lt v0, p1, :end
    move-object v4, v2
    move-object v2, v3
    move-object v3, v4
    :end

    # propagate through the undecidable objects
    invoke-static {v2, p1}, LMyClass_Binder;->exec(Landroid/os/Binder;I)I
    move-result v4
    invoke-static {v3, v4}, LMyClass_Binder;->exec(Landroid/os/Binder;I)I
    move-result v5

    return v5
.end method
