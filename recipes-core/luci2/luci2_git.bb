DESCRIPTION = "Basic utility library"
HOMEPAGE = "http://wiki.openwrt.org/doc/uci"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../Makefile;beginline=1;endline=5;md5=3ce05a39dbd458b46a410c5a9f266107"

SRC_URI = "\
    git://git.openwrt.org/project/luci2/ui.git;protocol=git;branch=master \
    file://100-luci2-fix-compiler-warning.patch \
    file://0001-Fix-segmentation-fault.patch \
    file://0002-Fix-rpcd-plugins-install-location.patch \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://luci.service', '', d)} \
"
SRC_URI_append_df-refkit-config = "file://luci.ruleset"
SRCREV = "e452ca693af5278ff2ddc69b6f8ed0f346c98fb1"

LUCI2 = "${WORKDIR}/git/luci2/"
S = "${LUCI2}/src"

inherit cmake systemd

DEPENDS = "libubox ubus rpcd"
RDEPENDS_${PN} = "rpcd uhttpd2"
RDEPENDS_${PN}_append_df-refkit-firewall += " nftables"

CFLAGS_append = " -Wno-unused-result"

do_install_append() {
    install -d ${D}/www/
    cp -r ${LUCI2}/htdocs/* ${D}/www/

    install -Dm 0644 ${LUCI2}/share/acl.d/luci2.json ${D}${datadir}/rpcd/acl.d/luci2.json
    install -Dm 0644 ${LUCI2}/share/menu.d/status.json ${D}${datadir}/rpcd/menu.d/status.json
    install -Dm 0644 ${LUCI2}/share/menu.d/system.json ${D}${datadir}/rpcd/menu.d/system.json
    install -Dm 0644 ${LUCI2}/share/menu.d/network.json ${D}${datadir}/rpcd/menu.d/network.json
    if [ -n "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '1', '', d)}" ]; then
        install -dm 0755 ${D}/${systemd_system_unitdir}
        install -Dm 0664 ${WORKDIR}/luci.service ${D}/${systemd_system_unitdir}/
    fi
}

do_install_append_df-refkit-firewall() {
    install -d ${D}${libdir}/firewall/services
    install -m 0644 ${WORKDIR}/luci.ruleset ${D}${libdir}/firewall/services/
}

FILES_${PN}_append_df-refkit-firewall += "${libdir}/firewall/services/luci.ruleset"
FILES_${PN} += "${datadir}/rpcd ${libdir}/rpcd /www"
FILES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}', '', d)}"
