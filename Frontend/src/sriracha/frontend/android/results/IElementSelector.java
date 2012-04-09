package sriracha.frontend.android.results;

import android.graphics.*;

public interface IElementSelector<T>
{
    public void setOnSelectListener(OnSelectListener onSelectListener);

    public boolean onSelect(T selectedElement);
    public void onDraw(Canvas canvas);

    public interface OnSelectListener<T>
    {
        public void onSelect(T selectedElement);
    }
}
