package sriracha.math.interfaces;

public interface IVector {
    
   public int getDimension();
    
   public IVector plus(IVector vector);
   public IVector minus(IVector vector);
   public IVector times(double vector);

    public IVector clone();



}
