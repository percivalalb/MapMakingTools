package mapmakingtools.client.screen.widget;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.state.Property;
import net.minecraft.tags.Tag;

public class ToggleBoxList<T> extends ScrollPane {

    private int noElements;
    private ToggleBoxGroup<T> toggleGroup = ToggleBoxGroup.single();

    public ToggleBoxList(int xIn, int yIn, int widthIn, int heightIn, @Nullable ToggleBoxList<T> previous) {
        super(xIn, yIn, widthIn, heightIn);

        if (previous != null) {

        }
    }

    public ToggleBoxList<T> setSelectionGroupManager(ToggleBoxGroup<T> manager) {
        this.toggleGroup = manager;
        return this;
    }

    public ToggleBoxList<T> setValues(T[] values, Function<T, Object> toStringFunc, @Nullable ToggleBoxList<?> previous) {
        return this.setValues(Lists.newArrayList(values), toStringFunc, previous);
    }

    public ToggleBoxList<T> setValues(Iterable<T> values, Function<T, Object> toStringFunc, @Nullable ToggleBoxList<?> previous) { // ? instead of T
        if (this.toggleGroup == null) {
            // throw error
        }

        this.clear(); // clear old values

        List<T> list = Lists.newArrayList(values);

        int i = 2;
        for (T value : values) {
            ToggleBoxWidget<T> box = new ToggleBoxWidget<>(this.x + 2, this.y + i, null, () -> value, this.toggleGroup::buttonClicked);
            box.setDisplayString(toStringFunc);

            this.widgets.add(box);
            i += 10;
            this.noElements++;
        }

        this.hiddenHeight = Math.max(0, i + 2 - this.height);

        if (previous != null) {
            this.clampScrollOffset(previous.scrollOffset);
        }

        return this;
    }

    public boolean selectValue(T value) {
        ToggleBoxWidget box = null;
        for (Object w : this.widgets) {
            if (w instanceof ToggleBoxWidget) {
                ToggleBoxWidget temp = (ToggleBoxWidget) w;
                if (temp.getValue().equals(value)) {
                    box = temp;
                    break;
                }
            }
        }

        if (box == null || box.isTicked()) {
            return false;
        }

        box.setTicked(true);
        return this.toggleGroup.buttonClicked(box);
    }

    /**
     * Resets the elements in the scroll widget
     */
    public void clear() {
        this.noElements = 0;
        this.widgets.clear();
        this.toggleGroup.selected.clear();
    }

    public int getNoElements() {
        return this.noElements;
    }

    public boolean isEmpty() {
        return this.noElements == 0;
    }

    public ToggleBoxGroup<T> getGroupManager() {
        return this.toggleGroup;
    }

    public static class ToggleBoxGroup<T> {

        public static final ToggleBoxGroup.Builder<?> DEFAULT = new ToggleBoxGroup.Builder<>().min(1).max(1);
        public static final ToggleBoxGroup.Builder<?> NO_LIMITS = new ToggleBoxGroup.Builder<String>().min(0).max(Integer.MAX_VALUE);

        private ISelectionChange<T> onChange;

        private final int minSelected;
        private final int maxSelected;
        private final LinkedHashMap<T, ToggleBoxWidget<T>> selected;

        private ToggleBoxGroup(int min, int max, @Nullable ISelectionChange<T> onChange) {
            this.minSelected = min;
            this.maxSelected = max;
            this.onChange = onChange;
            this.selected = Maps.newLinkedHashMap();
        }

        public boolean buttonClicked(AbstractTickButton btn) {
            if (!(btn instanceof ToggleBoxWidget)) {
                // Print error
                return false;
            }

            boolean ticked = btn.isTicked();

            @SuppressWarnings("unchecked")
            ToggleBoxWidget<T> box = (ToggleBoxWidget<T>) btn;
            T value = box.getValue();
            boolean isSelected = this.selected.containsKey(value);
            boolean change = false;

            if (ticked) {
                if (isSelected) { return false; } // Already selected then do nothing

                // If list is already at max capacity
                if (this.selected.size() >= this.maxSelected) {
                    T firstKey = this.selected.keySet().iterator().next(); // Find first value
                    this.selected.remove(firstKey).setTicked(false); // Remove from list and de-select tickbox
                }

                this.selected.put(value, box);
                change = true;
            } else if (isSelected) {
                // If box was unticked and was previous selected
                if (this.selected.size() > this.minSelected) {
                    this.selected.remove(value);
                    change = true;
                } else {
                 // If it would take below min re-tick the box
                    box.setTicked(true);
                }
            }

            if (change && this.onChange != null) {
                this.onChange.onChange(this.getSelected());
            }

            return change;
        }

        public boolean isEmpty() {
            return this.selected.isEmpty();
        }

        public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
            return this.selected.keySet().stream().map(mapper);
        }

        public interface ISelectionChange<T> {
            void onChange(List<T> selction);
        }

        public List<T> getSelected() {
            return Lists.newArrayList(this.selected.keySet());
        }

        @SuppressWarnings("unchecked")
        public static <E> ToggleBoxGroup<E> single() {
            return (ToggleBoxGroup<E>) DEFAULT.build();
        }

        @SuppressWarnings("unchecked")
        public static <E> ToggleBoxGroup<E> noLimits() {
            return (ToggleBoxGroup<E>) NO_LIMITS.build();
        }

        public static <E> Builder<E> builder(Class<E> type) {
            return new Builder<E>();
        }

        public static <T> Builder<Tag<T>> builderTag(Class<T> subType) {
            return new Builder<Tag<T>>();
        }

        public static Builder<Property<?>> builderProperty() {
            return new Builder<Property<?>>();
        }

        public static class Builder<T> {

            private int minSelected = 1;
            private int maxSelected = 1;
            private ISelectionChange<T> onChange = null;

            public Builder<T> min(int min) {
                this.minSelected = Math.max(0, min);
                return this;
            }

            public Builder<T> max(int max) {
                this.maxSelected = Math.max(1, max);
                return this;
            }

            public Builder<T> listen(ISelectionChange<T> onChange) {
                this.onChange = onChange;
                return this;
            }

            public ToggleBoxGroup<T> build() {
                if (this.minSelected > this.maxSelected) {
                    // TODO throw error
                }

                return new ToggleBoxGroup<>(this.minSelected, this.maxSelected, this.onChange);
            }
        }
    }

}
