.class public Luk/ac/cam/db538/dexter/tests/Test_Convert_ToShort;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "Convert: to short"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "int-to-short rX, [+]"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    int-to-short v0, p1
    return v0
    
.end method
