-- Prevents two rows for the same dish in the same cart. CartItemService already
-- merges quantity into an existing row when one is found, but that read-then-write
-- has no database-level guarantee: two concurrent "add to cart" requests for the
-- same dish could otherwise both pass the "not found" check and insert separately.
-- This constraint makes that impossible at the database level; the losing request
-- of such a race gets a clean 409 (see GlobalExceptionHandler#handleDataIntegrity)
-- instead of silently creating a duplicate row.
ALTER TABLE cart_items
    ADD CONSTRAINT uq_cart_items_cart_dish UNIQUE (cart_id, dish_id);
