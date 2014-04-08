package de.matthiasmann.twl.model;

public interface ListModelChangeListener {
    /**
     * New entries have been inserted. The existing entries starting at first
     * have been shifted. The range first-last (inclusive) are new.
     *
     * @param first the first new entry
     * @param last the last new entry. Must be >= first.
     */
    public void entriesInserted(int first, int last);

    /**
     * Entries that were at the range first to last (inclusive) have been removed.
     * Entries that were following last (starting with last+1) have been shifted
     * to first.
     * @param first the first removed entry
     * @param last the last removed entry. Must be >= first.
     */
    public void entriesDeleted(int first, int last);

    /**
     * Entries in the range first to last (inclusive) have been changed.
     * @param first the first changed entry
     * @param last the last changed entry. Must be >= first.
     */
    public void entriesChanged(int first, int last);

    /**
     * The complete list was recreated. There is no known relation between
     * old and new entries. Also the number of entries has complete changed.
     */
    public void allChanged();
}
