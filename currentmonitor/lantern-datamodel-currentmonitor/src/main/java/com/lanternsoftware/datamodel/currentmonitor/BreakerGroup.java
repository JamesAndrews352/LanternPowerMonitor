package com.lanternsoftware.datamodel.currentmonitor;


import com.lanternsoftware.util.CollectionUtils;
import com.lanternsoftware.util.NullUtils;
import com.lanternsoftware.util.dao.annotations.DBSerializable;
import com.lanternsoftware.util.dao.annotations.PrimaryKey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@DBSerializable()
public class BreakerGroup {
	@PrimaryKey private String id;
	private int accountId;
	private String name;
	private List<BreakerGroup> subGroups;
	private List<Breaker> breakers;

	public BreakerGroup() {
	}

	public BreakerGroup(String _id, String _name, List<Breaker> _breakers) {
		id = _id;
		name = _name;
		breakers = _breakers;
	}

	public BreakerGroup(String _id, String _name, List<BreakerGroup> _subGroups, List<Breaker> _breakers) {
		id = _id;
		name = _name;
		subGroups = _subGroups;
		breakers = _breakers;
	}

	public String getId() {
		return id;
	}

	public void setId(String _id) {
		id = _id;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int _accountId) {
		accountId = _accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String _name) {
		name = _name;
	}

	public List<BreakerGroup> getSubGroups() {
		return subGroups;
	}

	public void setSubGroups(List<BreakerGroup> _subGroups) {
		subGroups = _subGroups;
	}

	public List<Breaker> getBreakers() {
		return breakers;
	}

	public void setBreakers(List<Breaker> _breakers) {
		breakers = _breakers;
	}

	public List<Breaker> getAllBreakers() {
		List<Breaker> allBreakers = new ArrayList<>();
		getAllBreakers(allBreakers);
		return allBreakers;
	}

	private void getAllBreakers(List<Breaker> _breakers) {
		if (breakers != null)
			_breakers.addAll(breakers);
		for (BreakerGroup group : CollectionUtils.makeNotNull(subGroups)) {
			group.getAllBreakers(_breakers);
		}
	}

	public List<String> getAllBreakerKeys() {
		return CollectionUtils.transform(getAllBreakers(), Breaker::getKey);
	}

	public List<BreakerGroup> getAllBreakerGroups() {
		List<BreakerGroup> allGroups = new ArrayList<>();
		getAllBreakerGroups(allGroups);
		return allGroups;
	}

	private void getAllBreakerGroups(List<BreakerGroup> _groups) {
		_groups.add(this);
		for (BreakerGroup group : CollectionUtils.makeNotNull(subGroups)) {
			group.getAllBreakerGroups(_groups);
		}
	}

	public List<String> getAllBreakerGroupIds() {
		return CollectionUtils.transform(getAllBreakerGroups(), BreakerGroup::getId);
	}

	public Breaker getBreaker(String _breakerKey) {
		for (Breaker b : CollectionUtils.makeNotNull(breakers)) {
			if (NullUtils.isEqual(b.getKey(), _breakerKey))
				return b;
		}
		for (BreakerGroup group : CollectionUtils.makeNotNull(subGroups)) {
			Breaker b = group.getBreaker(_breakerKey);
			if (b != null)
				return b;
		}
		return null;
	}

	public String getGroupIdForBreaker(Breaker _breaker) {
		return getGroupIdForBreaker(_breaker.getKey());
	}

	public String getGroupIdForBreaker(int _panel, int _space) {
		return getGroupIdForBreaker(Breaker.key(_panel, _space));
	}

	public String getGroupIdForBreaker(String _breakerKey) {
		BreakerGroup group = getGroupForBreaker(_breakerKey);
		return group != null ? group.getId() : null;
	}

	public BreakerGroup getGroupForBreaker(Breaker _breaker) {
		return getGroupForBreaker(_breaker.getKey());
	}

	public BreakerGroup getGroupForBreaker(int _panel, int _space) {
		return getGroupForBreaker(Breaker.key(_panel, _space));
	}

	public BreakerGroup getGroupForBreaker(String _breakerKey) {
		if (_breakerKey == null)
			return null;
		Breaker b = CollectionUtils.filterOne(breakers, _b->_breakerKey.equals(_b.getKey()));
		if (b != null)
			return this;
		for (BreakerGroup subGroup : CollectionUtils.makeNotNull(subGroups)) {
			BreakerGroup group = subGroup.getGroupForBreaker(_breakerKey);
			if (group != null)
				return group;
		}
		return null;
	}

	public BreakerGroup findParentGroup(BreakerGroup _group) {
		if (CollectionUtils.contains(subGroups, _group))
			return this;
		for (BreakerGroup subGroup : CollectionUtils.makeNotNull(subGroups)) {
			BreakerGroup parent = subGroup.findParentGroup(_group);
			if (parent != null)
				return parent;
		}
		return null;
	}

	public boolean removeInvalidGroups(Set<Integer> _validPanels) {
		if (subGroups != null)
			subGroups.removeIf(_g->!_g.removeInvalidGroups(_validPanels));
		if (breakers != null)
			breakers.removeIf(_b->(_b.getType() == null) || (_b.getType() == BreakerType.EMPTY) || !_validPanels.contains(_b.getPanel()));
		return CollectionUtils.isNotEmpty(subGroups) || CollectionUtils.isNotEmpty(breakers);
	}

	@Override
	public boolean equals(Object _o) {
		if (this == _o) return true;
		if (_o == null || getClass() != _o.getClass()) return false;
		BreakerGroup that = (BreakerGroup) _o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
