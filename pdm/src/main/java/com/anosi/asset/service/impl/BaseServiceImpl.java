package com.anosi.asset.service.impl;

import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.i18n.I18nComponent;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.OplogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Transactional
public abstract class BaseServiceImpl<T,ID extends Serializable> {

	@Autowired
	protected SessionComponent sessionComponent;
	@Autowired
	protected I18nComponent i18nComponent;
	@Autowired
	protected OplogService oplogService;

	public abstract PagingAndSortingRepository<T,ID> getRepository();

	public <S extends T> S save(S entity) {
		return getRepository().save(entity);
	}

	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		return getRepository().save(entities);
	}

	public T findOne(ID id) {
		return getRepository().findOne(id);
	}

	public boolean exists(ID id) {
		return getRepository().exists(id);
	}

	public Iterable<T> findAll() {
		return getRepository().findAll();
	}

	public Iterable<T> findAll(Iterable<ID> ids) {
		return getRepository().findAll(ids);
	}

	public long count() {
		return getRepository().count();
	}

	public void delete(ID id) {
		getRepository().delete(id);
	}

	public void delete(T entity) {
		getRepository().delete(entity);
	}

	public void delete(Iterable<? extends T> entities) {
		getRepository().delete(entities);
	}

	public void deleteAll() {
		getRepository().deleteAll();
	}

	public Iterable<T> findAll(Sort sort) {
		return getRepository().findAll(sort);
	}

	public Page<T> findAll(Pageable pageable) {
		return getRepository().findAll(pageable);
	}

	/**
	 * 保存操作日志
	 * @param target
	 * @param operation
	 * @param content
	 */
	protected void saveLog(String target, Oplog.Operation operation, String content) {
		Oplog oplog = new Oplog(sessionComponent.getCurrentUser().getName(), target, operation, content);
		oplogService.save(oplog);
	}
}
