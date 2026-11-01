package com.glamoraa.service;

import com.glamoraa.dao.OrderDAO;
import com.glamoraa.dao.ReviewDAO;
import com.glamoraa.dao.impl.OrderDAOImpl;
import com.glamoraa.dao.impl.ReviewDAOImpl;
import com.glamoraa.model.Review;
import com.glamoraa.util.BusinessException;
import com.glamoraa.util.Tx;
import com.glamoraa.util.Validator;

public class ReviewService {
    private final ReviewDAO reviews = new ReviewDAOImpl();
    private final OrderDAO orders = new OrderDAOImpl();

    /** Only customers whose order containing this product was DELIVERED can review it (once). */
    public void submit(long userId, long productId, int rating, String title, String body) {
        title = Validator.clean(title);
        body = Validator.clean(body);
        if (rating < 1 || rating > 5) throw new BusinessException("Choose a rating from 1 to 5.");
        if (title.length() > 120 || body.length() > 2000) throw new BusinessException("Your review is too long.");
        final String t = title, b = body;
        Tx.write(c -> {
            if (!orders.hasDelivered(c, userId, productId)) throw new BusinessException("You can review a product after your order has been delivered.");
            if (reviews.exists(c, userId, productId)) throw new BusinessException("You have already reviewed this product.");
            Review r = new Review();
            r.setProductId(productId); r.setUserId(userId); r.setRating(rating);
            r.setTitle(t.isEmpty() ? null : t); r.setBody(b.isEmpty() ? null : b);
            reviews.insert(c, r);
            return null;
        });
    }
}
