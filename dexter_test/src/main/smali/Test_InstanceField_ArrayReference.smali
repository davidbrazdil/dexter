.class public LTest_InstanceField_ArrayReference;
.super LPropagationTest;


# instance fields
.field private X:[Ljava/lang/Object;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, LPropagationTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "IField: references array"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "this.X = new Object[[+]]; return this.X.length;"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # size mod 4
    rem-int/lit8 p1, p1, 4

    # create object
    new-array v2, p1, [Ljava/lang/Object;

    # propagate
    iput-object v2, p0, LTest_InstanceField_ArrayReference;->X:[Ljava/lang/Object;
    iget-object v1, p0, LTest_InstanceField_ArrayReference;->X:[Ljava/lang/Object;

    # retrieve some primitive from the object
    array-length v0, v1

    return v0
    
.end method
