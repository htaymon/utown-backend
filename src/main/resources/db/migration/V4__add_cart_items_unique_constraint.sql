-- Prevents duplicate cart items for the same cart and dish.
ALTER TABLE cart_items
    ADD CONSTRAINT uq_cart_items_cart_dish UNIQUE (cart_id, dish_id);
