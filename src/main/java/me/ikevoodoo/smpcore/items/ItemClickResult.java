package me.ikevoodoo.smpcore.items;

public record ItemClickResult(ItemClickState state, boolean cancel) {

    public boolean shouldCancel() {
        return cancel() && state() == ItemClickState.SUCCESS;
    }

}
