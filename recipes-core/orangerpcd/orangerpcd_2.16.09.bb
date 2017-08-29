# Copyright (C) 2016 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Revolution RPC Server (RevoRPCD)"
HOMEPAGE = "https://github.com/mkschreder/jucid"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ecb319022da02987a5c1a92120412393"
SECTION = "apps"

SRCREV = "326c72fa702939c33e0207a2b5e7bff2b0bbee8d"
SRC_URI = "git://github.com/mkschreder/orangerpcd \
           file://0001-Use-_DEFAULT_SOUCE-inplace-of-_BSD_SOURCE.patch \
           file://0002-fix-build-failure-when-builddir-is-different-from-so.patch \
           file://0003-Fix-build-failure-in-parallel-build.patch \
           file://0004-fix-compiler-warning.patch \
           file://orange.shadow \
           file://orange.config \
           "
S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS += "libblobpack libutype libusys uci libwebsockets rpcd ubus lua5.1 luajit"
RDEPENDS_${PN} += "luaposix"

OECMAKE_C_FLAGS += "-I${STAGING_INCDIR}/lua5.1 -DLUA_COMPAT_5_1"
CFLAGS_append = " -Wno-unused-result -Wno-implicit-fallthrough -Wno-format-truncation"
EXTRA_OECMAKE += "-DLUAPATH=${libdir}/lua/5.1"
PACKAGECONFIG ??= ""
PACKAGECONFIG[parallel] = "--enable-parallel, --disable-parallel"

do_install_append() {
    install -d ${D}${datadir}/lua/5.1/orange
    install -Dm 0755  ${S}/lualib/orange/*.lua ${D}${datadir}/lua/5.1/orange/

    install -d ${D}${sysconfdir}/orange
    install -m0600 ${WORKDIR}/orange.shadow ${D}${sysconfdir}/orange/shadow

    install -d ${D}${sysconfdir}/config
    install -m0644 ${WORKDIR}/orange.config ${D}${sysconfdir}/config/orange
}

FILES_${PN} += "${datadir}/lua/5.1 ${sysconfdir}"