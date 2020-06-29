package thomson.laurel.beth.thymesaver.UI;

import android.content.Context;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.material.chip.ChipDrawable;

import thomson.laurel.beth.thymesaver.R;

public class CategoryEditText extends androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView {
    public CategoryEditText(Context context) {
        super(context);
    }

    public CategoryEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setCustomAdapter(ArrayAdapter<CharSequence> categoryAdapter) {
        super.setAdapter(categoryAdapter);
        super.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        super.setThreshold(1);
        super.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String categoryName = ((CharSequence) adapterView.getItemAtPosition(i)).toString();
                createCategoryChip(categoryName);
            }
        });
    }

    private void createCategoryChip(String categoryName) {
        ChipDrawable chip = ChipDrawable.createFromResource(getContext(), R.xml.standalone_chip);
        ImageSpan span = new ImageSpan(chip);
        int cursorPosition = this.getSelectionStart();
        int spanLength = categoryName.length() + 2;
        Editable text = this.getText();
        chip.setText(categoryName);
        chip.setBounds(0, 0, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
        text.setSpan(span, cursorPosition - spanLength, cursorPosition, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }
}
