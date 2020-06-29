package thomson.laurel.beth.thymesaver.UI;

import android.content.Context;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;

import java.util.ArrayList;
import java.util.List;

import thomson.laurel.beth.thymesaver.R;

public class CategoryEditText extends androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView {
    private List<ChipDrawable> mChips = new ArrayList<>();
    private int mLastChipPosition = -1;

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
        chip.setBounds(10, 0, chip.getIntrinsicWidth() + 10, chip.getIntrinsicHeight());
        text.setSpan(span, cursorPosition - spanLength, cursorPosition, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mChips.add(chip);
        mLastChipPosition += spanLength;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (mChips != null && mLastChipPosition != -1 && start == mLastChipPosition) {
            int startPos = getText().length() - mChips.get(mChips.size() - 1).getText().length() - 1;
            getText().delete(startPos, getText().length());
            mChips.remove(mChips.size() - 1);
            mLastChipPosition = startPos - 1;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            getText().append(", ");
            String newChip = this.getText().toString().substring(mLastChipPosition + 1, this.getText().length() - 2);
            createCategoryChip(newChip);
        }
        return super.onKeyDown(keyCode, event);
    }
}
