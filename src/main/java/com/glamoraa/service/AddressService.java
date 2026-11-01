package com.glamoraa.service;

import com.glamoraa.dao.AddressDAO;
import com.glamoraa.dao.impl.AddressDAOImpl;
import com.glamoraa.model.Address;
import com.glamoraa.util.FormException;
import com.glamoraa.util.NotFoundException;
import com.glamoraa.util.Tx;
import com.glamoraa.util.Validator;
import java.util.List;

public class AddressService {
    private static final int MAX_ADDRESSES = 10;
    private final AddressDAO addresses = new AddressDAOImpl();

    public List<Address> list(long userId) { return Tx.read(c -> addresses.findByUser(c, userId)); }

    public Address get(long userId, long id) {
        Address a = Tx.read(c -> addresses.findForUser(c, id, userId));
        if (a == null) throw new NotFoundException("Address not found");
        return a;
    }

    /** Insert (a.id == 0) or update. The user id always comes from the session, never from the form. */
    public void save(long userId, Address a) {
        a.setUserId(userId);
        Validator v = new Validator()
                .required("label", a.getLabel(), "Label", 40)
                .required("recipientName", a.getRecipientName(), "Recipient name", 120)
                .required("phone", a.getPhone(), "Phone number", 20).phone("phone", a.getPhone(), true)
                .required("line1", a.getLine1(), "Address line 1", 190).optional("line2", a.getLine2(), "Address line 2", 190)
                .required("city", a.getCity(), "City", 80).required("state", a.getState(), "State", 80)
                .required("postalCode", a.getPostalCode(), "Postal code", 12).postal("postalCode", a.getPostalCode())
                .required("country", a.getCountry(), "Country", 60);
        if (!v.ok()) throw new FormException(v.errors());
        Tx.write(c -> {
            List<Address> existing = addresses.findByUser(c, userId);
            if (a.getId() == 0) {
                if (existing.size() >= MAX_ADDRESSES) throw new FormException(new Validator().error("label", "You can save up to " + MAX_ADDRESSES + " addresses.").errors());
                if (existing.isEmpty()) a.setDefaultAddress(true);
                if (a.isDefaultAddress()) addresses.clearDefault(c, userId);
                addresses.insert(c, a);
            } else {
                if (a.isDefaultAddress()) addresses.clearDefault(c, userId);
                if (addresses.update(c, a) == 0) throw new NotFoundException("Address not found");
            }
            return null;
        });
    }

    public void delete(long userId, long id) {
        Tx.write(c -> {
            if (addresses.delete(c, id, userId) == 0) throw new NotFoundException("Address not found");
            List<Address> rest = addresses.findByUser(c, userId);
            if (!rest.isEmpty() && rest.stream().noneMatch(Address::isDefaultAddress)) addresses.markDefault(c, rest.get(0).getId(), userId);
            return null;
        });
    }

    public void setDefault(long userId, long id) {
        Tx.write(c -> {
            if (addresses.findForUser(c, id, userId) == null) throw new NotFoundException("Address not found");
            addresses.clearDefault(c, userId);
            addresses.markDefault(c, id, userId);
            return null;
        });
    }
}
