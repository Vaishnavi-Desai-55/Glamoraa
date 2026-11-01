/* Glamoraa front-end behaviour. Vanilla JS, no dependencies, no inline scripts (CSP: script-src 'self'). */
(function () {
    'use strict';

    var body = document.body;
    var ctx = body.getAttribute('data-ctx') || '';
    var csrf = (document.querySelector('meta[name="csrf-token"]') || {}).content || '';
    var loggedIn = body.getAttribute('data-auth') === '1';
    var reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    function $(sel, root) { return (root || document).querySelector(sel); }
    function $$(sel, root) { return Array.prototype.slice.call((root || document).querySelectorAll(sel)); }
    function el(tag, cls, text) { var e = document.createElement(tag); if (cls) e.className = cls; if (text != null) e.textContent = text; return e; }

    /* ---------- Toasts ------------------------------------------------------ */
    var region = $('#toast-region');
    function toast(message, type) {
        if (!region || !message) return;
        var t = el('div', 'toast ' + (type || ''), message);
        t.setAttribute('role', type === 'error' ? 'alert' : 'status');
        region.appendChild(t);
        setTimeout(function () {
            t.classList.add('is-leaving');
            setTimeout(function () { t.remove(); }, 280);
        }, 4200);
    }
    if (region && region.dataset.message) toast(region.dataset.message, region.dataset.type);

    /* ---------- fetch helper (JSON in/out, CSRF header) -------------------- */
    function api(url, data, method) {
        var opts = { method: method || (data ? 'POST' : 'GET'), headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest', 'X-CSRF-Token': csrf }, credentials: 'same-origin' };
        if (data) opts.body = new URLSearchParams(data);
        return fetch(url, opts).then(function (r) {
            return r.json().catch(function () { return { ok: false, message: 'Unexpected response.' }; }).then(function (j) {
                if (r.status === 401 && j.login) { window.location.href = j.login; throw new Error('login'); }
                if (!r.ok || j.ok === false) { var err = new Error(j.message || 'Something went wrong.'); err.handled = true; throw err; }
                return j;
            });
        });
    }
    function fail(e) { if (e && e.message !== 'login') toast(e.message || 'Something went wrong. Please try again.', 'error'); }

    /* ---------- Header scroll state ---------------------------------------- */
    var header = $('.site-header');
    function onScroll() { if (header) header.classList.toggle('is-scrolled', window.scrollY > 12); }
    window.addEventListener('scroll', onScroll, { passive: true });
    onScroll();

    /* ---------- Drawers / overlays (mobile nav, search, cart) ------------- */
    var scrim = $('#scrim');
    var openPanel = null, lastFocus = null;
    function focusables(root) { return $$('a[href], button:not([disabled]), input:not([disabled]), select, textarea, [tabindex]:not([tabindex="-1"])', root).filter(function (n) { return n.offsetParent !== null; }); }
    function openOverlay(id) {
        var p = document.getElementById(id);
        if (!p) return;
        if (openPanel) closeOverlay(true);
        lastFocus = document.activeElement;
        p.classList.add('is-open');
        p.setAttribute('aria-hidden', 'false');
        if (scrim) scrim.classList.add('is-open');
        body.style.overflow = 'hidden';
        openPanel = p;
        $$('[data-open="' + id + '"]').forEach(function (b) { b.setAttribute('aria-expanded', 'true'); });
        setTimeout(function () { var f = $('[data-autofocus]', p) || focusables(p)[0]; if (f) f.focus(); }, 60);
    }
    function closeOverlay(keepFocus) {
        if (!openPanel) return;
        openPanel.classList.remove('is-open');
        openPanel.setAttribute('aria-hidden', 'true');
        $$('[data-open="' + openPanel.id + '"]').forEach(function (b) { b.setAttribute('aria-expanded', 'false'); });
        openPanel = null;
        if (scrim) scrim.classList.remove('is-open');
        body.style.overflow = '';
        if (!keepFocus && lastFocus && lastFocus.focus) lastFocus.focus();
    }
    document.addEventListener('click', function (e) {
        var opener = e.target.closest('[data-open]');
        if (opener) {
            if (opener.dataset.open === 'cart-drawer' && !(loggedIn && !body.hasAttribute('data-no-drawer'))) return; // plain link to /cart
            e.preventDefault();
            if (opener.dataset.open === 'cart-drawer') loadCart();
            openOverlay(opener.dataset.open);
            return;
        }
        if (e.target.closest('[data-close]') || e.target === scrim) closeOverlay();
    });
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') closeOverlay();
        if (e.key === 'Tab' && openPanel) {           // keep keyboard focus inside the open panel
            var f = focusables(openPanel);
            if (!f.length) return;
            var first = f[0], last = f[f.length - 1];
            if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
            else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
        }
    });

    /* ---------- Cart drawer (renders ONLY what the server returned) ------- */
    function setCount(n) {
        $$('[data-cart-count]').forEach(function (b) {
            b.textContent = n; b.hidden = !n;
            b.classList.remove('bump'); void b.offsetWidth; if (n) b.classList.add('bump');
        });
    }
    function renderCart(c) {
        setCount(c.count);
        var list = $('#cart-items');
        if (!list) return;
        list.textContent = '';
        $('#cart-empty').hidden = !c.empty;
        $('#cart-foot').hidden = c.empty;
        c.items.forEach(function (i) {
            var row = el('div', 'mini-item');
            var img = el('img'); img.src = i.image; img.alt = ''; img.loading = 'lazy';
            var info = el('div');
            var h = el('h3'); var a = el('a', null, i.name); a.href = i.url; h.appendChild(a);
            info.appendChild(h);
            info.appendChild(el('div', 'meta', i.size + ' \u00B7 ' + i.color));
            if (!i.available) info.appendChild(el('div', 'warn', 'Unavailable or low stock - review your bag'));
            var line = el('div', 'line');
            line.appendChild(el('span', null, 'Qty ' + i.quantity + ' \u00B7 ' + i.unit));
            line.appendChild(el('strong', null, i.line));
            info.appendChild(line);
            var rm = el('button', 'btn-text', 'Remove'); rm.type = 'button'; rm.dataset.cartRemove = i.id; rm.style.marginTop = '6px';
            info.appendChild(rm);
            row.appendChild(img); row.appendChild(info);
            list.appendChild(row);
        });
        $('#cart-subtotal').textContent = c.subtotal;
        $('#cart-shipping').textContent = c.shipping;
        $('#cart-total').textContent = c.total;
    }
    function loadCart() { return api(ctx + '/cart/summary').then(renderCart).catch(fail); }
    document.addEventListener('click', function (e) {
        var rm = e.target.closest('[data-cart-remove]');
        if (!rm) return;
        rm.disabled = true;
        api(ctx + '/cart/remove', { itemId: rm.dataset.cartRemove }).then(function (c) { renderCart(c); toast(c.message); }).catch(function (err) { rm.disabled = false; fail(err); });
    });

    /* ---------- Add to bag (server must confirm before we show success) -- */
    document.addEventListener('submit', function (e) {
        var form = e.target.closest('form[data-add-to-cart]');
        if (!form) return;
        e.preventDefault();
        var btn = form.querySelector('button[type="submit"]');
        if (!form.variantId.value) { toast('Please choose a size and colour.', 'error'); return; }
        btn.classList.add('is-loading');
        api(ctx + '/cart/add', { productId: form.productId.value, variantId: form.variantId.value, quantity: form.quantity.value })
            .then(function (c) { renderCart(c); toast(c.message, 'success'); if (!body.hasAttribute('data-no-drawer')) openOverlay('cart-drawer'); })
            .catch(fail)
            .then(function () { btn.classList.remove('is-loading'); });
    });

    /* ---------- Wishlist toggle ------------------------------------------- */
    document.addEventListener('click', function (e) {
        var b = e.target.closest('[data-wish]');
        if (!b) return;
        e.preventDefault();
        if (!loggedIn) { window.location.href = ctx + '/login?next=' + encodeURIComponent(window.location.pathname.substring(ctx.length) + window.location.search); return; }
        b.disabled = true;
        api(ctx + '/wishlist/toggle', { productId: b.dataset.wish }).then(function (r) {
            $$('[data-wish="' + b.dataset.wish + '"]').forEach(function (x) {
                x.setAttribute('aria-pressed', r.wished ? 'true' : 'false');
                x.classList.remove('pop'); void x.offsetWidth; x.classList.add('pop');
            });
            var dot = $('#wish-dot'); if (dot) dot.hidden = !r.count;
            toast(r.message, 'success');
        }).catch(fail).then(function () { b.disabled = false; });
    });

    /* ---------- Generic AJAX forms (newsletter) --------------------------- */
    document.addEventListener('submit', function (e) {
        var f = e.target.closest('form[data-ajax]');
        if (!f) return;
        e.preventDefault();
        var btn = f.querySelector('button[type="submit"]');
        if (btn) btn.classList.add('is-loading');
        api(f.action, new FormData(f)).then(function (r) { toast(r.message, 'success'); f.reset(); }).catch(fail).then(function () { if (btn) btn.classList.remove('is-loading'); });
    });

    /* ---------- Normal forms: loading state + double-submit guard --------- */
    document.addEventListener('submit', function (e) {
        var f = e.target;
        if (e.defaultPrevented || !(f instanceof HTMLFormElement) || f.hasAttribute('data-add-to-cart') || f.hasAttribute('data-ajax')) return;
        var msg = f.getAttribute('data-confirm');
        if (msg && !window.confirm(msg)) { e.preventDefault(); return; }
        if (f.dataset.submitted) { e.preventDefault(); return; }
        if (f.hasAttribute('data-once') || f.hasAttribute('data-loading')) {
            f.dataset.submitted = '1';
            var btn = f.querySelector('button[type="submit"]');
            if (btn) { btn.classList.add('is-loading'); }
        }
    }, true);
    window.addEventListener('pageshow', function () {       // back button: make forms usable again
        $$('form[data-submitted]').forEach(function (f) { delete f.dataset.submitted; });
        $$('.btn.is-loading').forEach(function (b) { b.classList.remove('is-loading'); });
    });

    /* ---------- Quantity steppers ----------------------------------------- */
    document.addEventListener('click', function (e) {
        var b = e.target.closest('[data-qty-step]');
        if (!b) return;
        var wrap = b.closest('.qty'), input = $('input', wrap);
        var max = parseInt(input.max || '10', 10), min = parseInt(input.min || '1', 10);
        var next = Math.min(max, Math.max(min, (parseInt(input.value, 10) || min) + parseInt(b.dataset.qtyStep, 10)));
        if (next === parseInt(input.value, 10)) return;
        input.value = next;
        var form = wrap.closest('form[data-qty-form]');
        if (form) { clearTimeout(form._t); form._t = setTimeout(function () { form.submit(); }, 350); }
    });

    /* ---------- Product page: variant picker ------------------------------ */
    var vdata = $('#variant-data');
    if (vdata) {
        var variants = JSON.parse(vdata.textContent);
        var form = $('form[data-add-to-cart]');
        var sizeBtns = $$('[data-size]'), colorBtns = $$('[data-color]');
        var note = $('#stock-note'), qtyInput = $('input[name="quantity"]', form), addBtn = $('button[type="submit"]', form);
        var maxQty = parseInt(qtyInput.max, 10) || 10;
        var sel = { size: null, color: null };
        var find = function (s, c) { return variants.filter(function (v) { return v.size == s && v.color == c; })[0]; };
        function refresh() {
            sizeBtns.forEach(function (b) {
                var any = variants.some(function (v) { return v.size == b.dataset.size && v.stock > 0 && (!sel.color || v.color == sel.color); });
                b.disabled = !any; b.setAttribute('aria-pressed', String(sel.size == b.dataset.size));
                if (!any && sel.size == b.dataset.size) sel.size = null;
            });
            colorBtns.forEach(function (b) {
                var any = variants.some(function (v) { return v.color == b.dataset.color && v.stock > 0 && (!sel.size || v.size == sel.size); });
                b.disabled = !any; b.setAttribute('aria-pressed', String(sel.color == b.dataset.color));
                if (!any && sel.color == b.dataset.color) sel.color = null;
            });
            var cn = $('#chosen-color'); if (cn) { var cb = colorBtns.filter(function (b) { return sel.color == b.dataset.color; })[0]; cn.textContent = cb ? cb.dataset.name : ''; }
            var v = sel.size && sel.color ? find(sel.size, sel.color) : null;
            form.variantId.value = v ? v.id : '';
            addBtn.disabled = !v;
            if (v) {
                var cap = Math.min(v.stock, maxQty);
                qtyInput.max = cap; if (parseInt(qtyInput.value, 10) > cap) qtyInput.value = cap;
                note.textContent = v.stock <= 5 ? 'Only ' + v.stock + ' left in this size and colour.' : 'In stock.';
                note.className = 'stock-note' + (v.stock <= 5 ? ' low' : '');
            } else {
                note.textContent = sel.size || sel.color ? 'Choose a size and a colour to continue.' : '';
                note.className = 'stock-note';
            }
        }
        sizeBtns.forEach(function (b) { b.addEventListener('click', function () { sel.size = sel.size == b.dataset.size ? null : b.dataset.size; refresh(); }); });
        colorBtns.forEach(function (b) { b.addEventListener('click', function () { sel.color = sel.color == b.dataset.color ? null : b.dataset.color; refresh(); }); });
        if (sizeBtns.length === 1 && !sizeBtns[0].disabled) sel.size = sizeBtns[0].dataset.size;    // "One Size"
        if (colorBtns.length === 1 && !colorBtns[0].disabled) sel.color = colorBtns[0].dataset.color;
        refresh();
    }

    /* ---------- Gallery + lightbox ---------------------------------------- */
    var main = $('#gallery-main-img');
    if (main) {
        $$('.thumb').forEach(function (t) {
            t.addEventListener('click', function () {
                if (t.getAttribute('aria-current') === 'true') return;
                $$('.thumb').forEach(function (x) { x.removeAttribute('aria-current'); });
                t.setAttribute('aria-current', 'true');
                main.classList.add('is-fading');
                setTimeout(function () { main.src = t.dataset.src; main.alt = t.dataset.alt; main.classList.remove('is-fading'); }, reduceMotion ? 0 : 220);
            });
        });
        var box = $('#lightbox'), boxImg = $('#lightbox-img');
        $('#gallery-main').addEventListener('click', function () {
            if (!box || !box.showModal) return;
            boxImg.src = main.src; boxImg.alt = main.alt; box.showModal();
        });
        if (box) box.addEventListener('click', function (e) { if (e.target === box || e.target.closest('[data-lightbox-close]')) box.close(); });
    }

    /* ---------- Scroll reveal ---------------------------------------------- */
    var reveals = $$('[data-reveal]');
    if ('IntersectionObserver' in window && !reduceMotion) {
        var io = new IntersectionObserver(function (entries) {
            entries.forEach(function (en) { if (en.isIntersecting) { en.target.classList.add('is-visible'); io.unobserve(en.target); } });
        }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });
        reveals.forEach(function (n) {
            var siblings = n.parentElement ? $$('[data-reveal]', n.parentElement).filter(function (s) { return s.parentElement === n.parentElement; }) : [n];
            n.style.setProperty('--d', Math.min(siblings.indexOf(n), 8) * 70 + 'ms');   // staggered entrance
            io.observe(n);
        });
    } else { reveals.forEach(function (n) { n.classList.add('is-visible'); }); }

    /* ---------- Missing images fall back to the bundled placeholder -------- */
    document.addEventListener('error', function (e) {
        var img = e.target;
        if (img.tagName === 'IMG' && !img.dataset.fallback) { img.dataset.fallback = '1'; img.src = ctx + '/assets/images/placeholder.svg'; }
    }, true);
})();
