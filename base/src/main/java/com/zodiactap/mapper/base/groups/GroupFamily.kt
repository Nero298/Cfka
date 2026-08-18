package com.zodiactap.mapper.base.groups

data class GroupFamily(val group: Group?, val children: List<Group>, val parents: List<Group>)
