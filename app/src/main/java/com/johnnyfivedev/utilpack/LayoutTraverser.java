package com.johnnyfivedev.utilpack;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LayoutTraverser {
    private LayoutTraverser.Processor processor;

    public LayoutTraverser(LayoutTraverser.Processor processor) {
        this.processor = processor;
    }

    public static LayoutTraverser build(@NonNull LayoutTraverser.Processor processor) {
        return new LayoutTraverser(processor);
    }

    public void traverse(@Nullable View root) {
        if (root != null) {
            this.processor.process(root);
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;

                for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                    View child = viewGroup.getChildAt(i);
                    this.traverse(child);
                }
            }
        }

    }

    public interface Processor {
        void process(View view);
    }
}