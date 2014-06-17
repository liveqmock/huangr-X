(function (b) {
    b.fn.ajaxSubmit = function (s) {
        if (!this.length) {
            a("ajaxSubmit: skipping submit process - no element selected");
            return this
        }
        if (typeof s == "function") {
            s = {success:s}
        }
        var e = b.trim(this.attr("action"));
        if (e) {
            e = (e.match(/^([^#]+)/) || [])[1]
        }
        e = e || window.location.href || "";
        s = b.extend({url:e, type:this.attr("method") || "GET"}, s || {});
        var u = {};
        this.trigger("form-pre-serialize", [this, s, u]);
        if (u.veto) {
            a("ajaxSubmit: submit vetoed via form-pre-serialize trigger");
            return this
        }
        if (s.beforeSerialize && s.beforeSerialize(this, s) === false) {
            a("ajaxSubmit: submit aborted via beforeSerialize callback");
            return this
        }
        var m = this.formToArray(s.semantic);
        if (s.data) {
            s.extraData = s.data;
            for (var f in s.data) {
                if (s.data[f] instanceof Array) {
                    for (var g in s.data[f]) {
                        m.push({name:f, value:s.data[f][g]})
                    }
                } else {
                    m.push({name:f, value:s.data[f]})
                }
            }
        }
        if (s.beforeSubmit && s.beforeSubmit(m, this, s) === false) {
            a("ajaxSubmit: submit aborted via beforeSubmit callback");
            return this
        }
        this.trigger("form-submit-validate", [m, this, s, u]);
        if (u.veto) {
            a("ajaxSubmit: submit vetoed via form-submit-validate trigger");
            return this
        }
        var d = b.param(m);
        if (s.type.toUpperCase() == "GET") {
            s.url += (s.url.indexOf("?") >= 0 ? "&" : "?") + d;
            s.data = null
        } else {
            s.data = d
        }
        var t = this, l = [];
        if (s.resetForm) {
            l.push(function () {
                t.resetForm()
            })
        }
        if (s.clearForm) {
            l.push(function () {
                t.clearForm()
            })
        }
        if (!s.dataType && s.target) {
            var p = s.success || function () {
            };
            l.push(function (j) {
                b(s.target).html(j).each(p, arguments)
            })
        } else {
            if (s.success) {
                l.push(s.success)
            }
        }
        s.success = function (q, k) {
            for (var n = 0, j = l.length; n < j; n++) {
                l[n].apply(s, [q, k, t])
            }
        };
        var c = b("input:file", this).fieldValue();
        var r = false;
        for (var i = 0; i < c.length; i++) {
            if (c[i]) {
                r = true
            }
        }
        var h = false;
        if (s.iframe || r || h) {
            if (s.closeKeepAlive) {
                b.get(s.closeKeepAlive, o)
            } else {
                o()
            }
        } else {
            b.ajax(s)
        }
        this.trigger("form-submit-notify", [this, s]);
        return this;
        function o() {
            var w = t[0];
            if (b(":input[name=submit]", w).length) {
                alert('Error: Form elements must not be named "submit".');
                return
            }
            var q = b.extend({}, b.ajaxSettings, s);
            var G = b.extend(true, {}, b.extend(true, {}, b.ajaxSettings), q);
            var v = "jqFormIO" + (new Date().getTime());
            var C = b('<iframe id="' + v + '" name="' + v + '" src="about:blank" />');
            var E = C[0];
            C.css({position:"absolute", top:"-1000px", left:"-1000px"});
            var F = {aborted:0, responseText:null, responseXML:null, status:0, statusText:"n/a", getAllResponseHeaders:function () {
            }, getResponseHeader:function () {
            }, setRequestHeader:function () {
            }, abort:function () {
                this.aborted = 1;
                C.attr("src", "about:blank")
            }};
            var D = q.global;
            if (D && !b.active++) {
                b.event.trigger("ajaxStart")
            }
            if (D) {
                b.event.trigger("ajaxSend", [F, q])
            }
            if (G.beforeSend && G.beforeSend(F, G) === false) {
                G.global && b.active--;
                return
            }
            if (F.aborted) {
                return
            }
            var k = 0;
            var z = 0;
            var j = w.clk;
            if (j) {
                var x = j.name;
                if (x && !j.disabled) {
                    s.extraData = s.extraData || {};
                    s.extraData[x] = j.value;
                    if (j.type == "image") {
                        s.extraData[name + ".x"] = w.clk_x;
                        s.extraData[name + ".y"] = w.clk_y
                    }
                }
            }
            setTimeout(function () {
                var J = t.attr("target"), H = t.attr("action");
                w.setAttribute("target", v);
                if (w.getAttribute("method") != "POST") {
                    w.setAttribute("method", "POST")
                }
                if (w.getAttribute("action") != q.url) {
                    w.setAttribute("action", q.url)
                }
                if (!s.skipEncodingOverride) {
                    t.attr({encoding:"multipart/form-data", enctype:"multipart/form-data"})
                }
                if (q.timeout) {
                    setTimeout(function () {
                        z = true;
                        A()
                    }, q.timeout)
                }
                var I = [];
                try {
                    if (s.extraData) {
                        for (var K in s.extraData) {
                            I.push(b('<input type="hidden" name="' + K + '" value="' + s.extraData[K] + '" />').appendTo(w)[0])
                        }
                    }
                    C.appendTo("body");
                    E.attachEvent ? E.attachEvent("onload", A) : E.addEventListener("load", A, false);
                    w.submit()
                } finally {
                    w.setAttribute("action", H);
                    J ? w.setAttribute("target", J) : t.removeAttr("target");
                    b(I).remove()
                }
            }, 10);
            var y = 50;

            function A() {
                if (k++) {
                    return
                }
                E.detachEvent ? E.detachEvent("onload", A) : E.removeEventListener("load", A, false);
                var H = true;
                try {
                    if (z) {
                        throw"timeout"
                    }
                    var I, L;
                    L = E.contentWindow ? E.contentWindow.document : E.contentDocument ? E.contentDocument : E.document;
                    var M = q.dataType == "xml" || L.XMLDocument || b.isXMLDoc(L);
                    a("isXml=" + M);
                    if (!M && (L.body == null || L.body.innerHTML == "")) {
                        if (--y) {
                            k = 0;
                            setTimeout(A, 100);
                            return
                        }
                        a("Could not access iframe DOM after 50 tries.");
                        return
                    }
                    F.responseText = L.body ? L.body.innerHTML : null;
                    F.responseXML = L.XMLDocument ? L.XMLDocument : L;
                    F.getResponseHeader = function (O) {
                        var N = {"content-type":q.dataType};
                        return N[O]
                    };
                    if (q.dataType == "json" || q.dataType == "script") {
                        var n = L.getElementsByTagName("textarea")[0];
                        if (n) {
                            F.responseText = n.value
                        } else {
                            var K = L.getElementsByTagName("pre")[0];
                            if (K) {
                                F.responseText = K.innerHTML
                            }
                        }
                    } else {
                        if (q.dataType == "xml" && !F.responseXML && F.responseText != null) {
                            F.responseXML = B(F.responseText)
                        }
                    }
                    I = b.httpData(F, q.dataType)
                } catch (J) {
                    H = false;
                    b.handleError(q, F, "error", J)
                }
                if (H) {
                    q.success(I, "success");
                    if (D) {
                        b.event.trigger("ajaxSuccess", [F, q])
                    }
                }
                if (D) {
                    b.event.trigger("ajaxComplete", [F, q])
                }
                if (D && !--b.active) {
                    b.event.trigger("ajaxStop")
                }
                if (q.complete) {
                    q.complete(F, H ? "success" : "error")
                }
                setTimeout(function () {
                    C.remove();
                    F.responseXML = null
                }, 100)
            }

            function B(n, H) {
                if (window.ActiveXObject) {
                    H = new ActiveXObject("Microsoft.XMLDOM");
                    H.async = "false";
                    H.loadXML(n)
                } else {
                    H = (new DOMParser()).parseFromString(n, "text/xml")
                }
                return(H && H.documentElement && H.documentElement.tagName != "parsererror") ? H : null
            }
        }
    };
    b.fn.ajaxForm = function (c) {
        return this.ajaxFormUnbind().bind("submit.form-plugin",function () {
            b(this).ajaxSubmit(c);
            return false
        }).bind("click.form-plugin", function (g) {
                var d = b(g.target);
                if (!(d.is(":submit,input:image"))) {
                    return
                }
                var f = this;
                f.clk = g.target;
                if (g.target.type == "image") {
                    if (g.offsetX != undefined) {
                        f.clk_x = g.offsetX;
                        f.clk_y = g.offsetY
                    } else {
                        if (typeof b.fn.offset == "function") {
                            var h = d.offset();
                            f.clk_x = g.pageX - h.left;
                            f.clk_y = g.pageY - h.top
                        } else {
                            f.clk_x = g.pageX - g.target.offsetLeft;
                            f.clk_y = g.pageY - g.target.offsetTop
                        }
                    }
                }
                setTimeout(function () {
                    f.clk = f.clk_x = f.clk_y = null
                }, 10)
            })
    };
    b.fn.ajaxFormUnbind = function () {
        return this.unbind("submit.form-plugin click.form-plugin")
    };
    b.fn.formToArray = function (q) {
        var p = [];
        if (this.length == 0) {
            return p
        }
        var d = this[0];
        var h = q ? d.getElementsByTagName("*") : d.elements;
        if (!h) {
            return p
        }
        for (var k = 0, m = h.length; k < m; k++) {
            var e = h[k];
            var f = e.name;
            if (!f) {
                continue
            }
            if (q && d.clk && e.type == "image") {
                if (!e.disabled && d.clk == e) {
                    p.push({name:f, value:b(e).val()});
                    p.push({name:f + ".x", value:d.clk_x}, {name:f + ".y", value:d.clk_y})
                }
                continue
            }
            var r = b.fieldValue(e, true);
            if (r && r.constructor == Array) {
                for (var g = 0, c = r.length; g < c; g++) {
                    p.push({name:f, value:r[g]})
                }
            } else {
                if (r !== null && typeof r != "undefined") {
                    p.push({name:f, value:r})
                }
            }
        }
        if (!q && d.clk) {
            var l = b(d.clk), o = l[0], f = o.name;
            if (f && !o.disabled && o.type == "image") {
                p.push({name:f, value:l.val()});
                p.push({name:f + ".x", value:d.clk_x}, {name:f + ".y", value:d.clk_y})
            }
        }
        return p
    };
    b.fn.formSerialize = function (c) {
        return b.param(this.formToArray(c))
    };
    b.fn.fieldSerialize = function (d) {
        var c = [];
        this.each(function () {
            var h = this.name;
            if (!h) {
                return
            }
            var f = b.fieldValue(this, d);
            if (f && f.constructor == Array) {
                for (var g = 0, e = f.length; g < e; g++) {
                    c.push({name:h, value:f[g]})
                }
            } else {
                if (f !== null && typeof f != "undefined") {
                    c.push({name:this.name, value:f})
                }
            }
        });
        return b.param(c)
    };
    b.fn.fieldValue = function (h) {
        for (var g = [], e = 0, c = this.length; e < c; e++) {
            var f = this[e];
            var d = b.fieldValue(f, h);
            if (d === null || typeof d == "undefined" || (d.constructor == Array && !d.length)) {
                continue
            }
            d.constructor == Array ? b.merge(g, d) : g.push(d)
        }
        return g
    };
    b.fieldValue = function (c, j) {
        var e = c.name, p = c.type, q = c.tagName.toLowerCase();
        if (typeof j == "undefined") {
            j = true
        }
        if (j && (!e || c.disabled || p == "reset" || p == "button" || (p == "checkbox" || p == "radio") && !c.checked || (p == "submit" || p == "image") && c.form && c.form.clk != c || q == "select" && c.selectedIndex == -1)) {
            return null
        }
        if (q == "select") {
            var k = c.selectedIndex;
            if (k < 0) {
                return null
            }
            var m = [], d = c.options;
            var g = (p == "select-one");
            var l = (g ? k + 1 : d.length);
            for (var f = (g ? k : 0); f < l; f++) {
                var h = d[f];
                if (h.selected) {
                    var o = h.value;
                    if (!o) {
                        o = (h.attributes && h.attributes.value && !(h.attributes.value.specified)) ? h.text : h.value
                    }
                    if (g) {
                        return o
                    }
                    m.push(o)
                }
            }
            return m
        }
        return c.value
    };
    b.fn.clearForm = function () {
        return this.each(function () {
            b("input,select,textarea", this).clearFields()
        })
    };
    b.fn.clearFields = b.fn.clearInputs = function () {
        return this.each(function () {
            var d = this.type, c = this.tagName.toLowerCase();
            if (d == "text" || d == "password" || c == "textarea") {
                this.value = ""
            } else {
                if (d == "checkbox" || d == "radio") {
                    this.checked = false
                } else {
                    if (c == "select") {
                        this.selectedIndex = -1
                    }
                }
            }
        })
    };
    b.fn.resetForm = function () {
        return this.each(function () {
            if (typeof this.reset == "function" || (typeof this.reset == "object" && !this.reset.nodeType)) {
                this.reset()
            }
        })
    };
    b.fn.enable = function (c) {
        if (c == undefined) {
            c = true
        }
        return this.each(function () {
            this.disabled = !c
        })
    };
    b.fn.selected = function (c) {
        if (c == undefined) {
            c = true
        }
        return this.each(function () {
            var d = this.type;
            if (d == "checkbox" || d == "radio") {
                this.checked = c
            } else {
                if (this.tagName.toLowerCase() == "option") {
                    var e = b(this).parent("select");
                    if (c && e[0] && e[0].type == "select-one") {
                        e.find("option").selected(false)
                    }
                    this.selected = c
                }
            }
        })
    };
    function a() {
        if (b.fn.ajaxSubmit.debug && window.console && window.console.log) {
            window.console.log("[jquery.form] " + Array.prototype.join.call(arguments, ""))
        }
    }
})(jQuery);