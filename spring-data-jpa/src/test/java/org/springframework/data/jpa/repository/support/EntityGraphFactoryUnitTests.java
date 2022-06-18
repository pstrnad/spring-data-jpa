/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.repository.support;

import static java.util.Arrays.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Subgraph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link EntityGraphFactory}.
 *
 * @author Jens Schauder
 * @author Petr Strnad
 */
@SuppressWarnings("rawtypes")
class EntityGraphFactoryUnitTests {

	EntityManager em = mock(EntityManager.class);
	EntityGraph entityGraph;

	@BeforeEach
	void beforeEach() {

		entityGraph = mock(EntityGraph.class, RETURNS_DEEP_STUBS);
		when(em.createEntityGraph(DummyEntity.class)).thenReturn(entityGraph);
	}

	// GH-2329
	@Test
	void simpleSetOfPropertiesGetRegistered() {

		HashSet<String> properties = new HashSet<>(asList("one", "two"));

		entityGraph = EntityGraphFactory.create(em, DummyEntity.class, properties);

		verify(entityGraph).addAttributeNodes("one");
		verify(entityGraph).addAttributeNodes("two");
	}

	// GH-2329
	@Test
	void setOfCompositePropertiesGetRegisteredPiecewise() {

		HashSet<String> properties = new HashSet<>(asList("one.one", "one.two", "eins.zwei.drei"));

		entityGraph = EntityGraphFactory.create(em, DummyEntity.class, properties);

		verify(entityGraph).addSubgraph("one");
		Subgraph<?> one = entityGraph.addSubgraph("one");
		verify(one).addAttributeNodes("one");
		verify(one).addAttributeNodes("two");

		verify(entityGraph).addSubgraph("eins");
		Subgraph<?> eins = entityGraph.addSubgraph("eins");
		verify(eins).addSubgraph("zwei");
		Subgraph<?> zwei = eins.addSubgraph("zwei");
		verify(zwei).addAttributeNodes("drei");
	}

	private static class DummyEntity {
		DummyEntity one;
		DummyEntity two;
		DummyEntity eins;
		DummyEntity zwei;
		DummyEntity drei;
	}
}
