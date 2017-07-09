# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "OpenWrt UBUS RPC server"
HOMEPAGE = "http://git.openwrt.org/?p=project/rpcd.git;a=summary"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://main.c;beginline=1;endline=18;md5=da5faf55ed0618f0dde1c88e76a0fc74"
SECTION = "base"
DEPENDS = "json-c libubox ubus uci"

SRCREV = "0577cfc1acdbaf30c31090e75045ba58d6dd8a78"
SRC_URI = "git://git.openwrt.org/project/rpcd.git \
           file://0001-cmake-Install-plugins-to-libdir-rpcd-insteadof-libdi.patch \
           file://rpcd.config \
           file://rpcd.init \
           file://rpcd.service \
           "

inherit cmake pkgconfig

S = "${WORKDIR}/git"

CFLAGS_append = " -Wno-unused-result"

PACKAGECONFIG ??= "rpc-sys file"
PACKAGECONFIG[iwinfo] = "-DIWINFO_SUPPORT=ON, -DIWINFO_SUPPORT=OFF, iwinfo"
PACKAGECONFIG[rpc-sys]  = "-DRPCSYS_SUPPORT=ON, -DRPCSYS_SUPPORT=OFF"
PACKAGECONFIG[file] = "-DFILE_SUPPORT=ON, -DFILE_SUPPORT=OFF"

do_install_append() {
    install -d ${D}${includedir}/rpcd
    install -m 0644 ${S}/include/rpcd/* ${D}${includedir}/rpcd/
    install -Dm 0755 ${WORKDIR}/rpcd.config ${D}${sysconfdir}/config/rpcd
    install -Dm 0755 ${WORKDIR}/rpcd.init ${D}${sysconfdir}/init.d/rpcd

    mkdir -p ${D}${sysconfdir}/rc.d
    ln -s ../init.d/rpcd ${D}${sysconfdir}/rc.d/S12rpcd

    if [ "${base_sbindir}" != "${sbindir}" ]; then
        mkdir -p ${D}/${base_sbindir}
        ln -s ${sbindir}/rpcd ${D}/${base_sbindir}/rpcd
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)};
    then
        install -dm 0755 ${D}/${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/rpcd.service ${D}/${systemd_system_unitdir}
    fi
}

FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/* ${base_sbindir} ${sbindir}"
FILES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}', '', d)}"
