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
    private List<Integer> mChipPositions = new ArrayList<>();
    private ArrayAdapter<CharSequence> mAdaper;

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
        mAdaper = categoryAdapter;
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

    private void removeElementFromAdapter(CharSequence element) {
        mAdaper.remove(element);
    }

    private void addElementToAdapter(CharSequence element) {
        mAdaper.add(element);
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (ChipDrawable chip : mChips) {
            categories.add(chip.getText().toString());
        }
        return categories;
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
        int chipPosition = lastChipPosition() + spanLength;
        mChipPositions.add(chipPosition);
        removeElementFromAdapter(chip.getText());
    }

    private int lastChipPosition() {
        if (mChipPositions.size() == 0) {
            return -1;
        }
        return mChipPositions.get(mChipPositions.size() - 1);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (mChipPositions == null) { return; }

        removeChip(start);
    }

    private void removeChip(int start) {
        int chipToRemovePosition = -1;
        for (int i = 0; i < mChipPositions.size(); i++) {
            int chipPosition = mChipPositions.get(i);
            if (start == chipPosition) {
                ChipDrawable chip = mChips.get(i);
                int startPos = start - chip.getText().length() - 1;
                getText().delete(startPos, startPos + chip.getText().length() + 1);
                chipToRemovePosition = i;
                break;
            }
        }

        if (chipToRemovePosition != -1 && chipToRemovePosition < mChipPositions.size()) {
            ChipDrawable removedChip = mChips.remove(chipToRemovePosition);
            mChipPositions.remove(chipToRemovePosition);
            addElementToAdapter(removedChip.getText());

            //need to update all the chip positions for the chips after the removed chip
            for (int i = chipToRemovePosition; i < mChipPositions.size(); i++) {
                ChipDrawable chip = mChips.get(i);
                int newPosition;
                if (i == 0) {
                    newPosition = chip.getText().length() + 1;
                } else {
                    newPosition = mChipPositions.get(i-1) + chip.getText().length() + 1;
                }
                mChipPositions.set(i, newPosition);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            addCustomChip();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addCustomChip() {
        getText().append(", ");
        String newChip = this.getText().toString().substring(lastChipPosition() + 1, this.getText().length() - 2);
        createCategoryChip(newChip);
    }
}
