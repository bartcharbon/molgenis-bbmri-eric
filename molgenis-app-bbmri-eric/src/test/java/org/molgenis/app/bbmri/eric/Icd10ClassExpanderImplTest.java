package org.molgenis.app.bbmri.eric;

import com.google.common.collect.Sets;
import org.molgenis.data.Entity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class Icd10ClassExpanderImplTest
{
	private Icd10ClassExpanderImpl icd10ClassExpanderImpl;

	@BeforeMethod
	public void setUp() throws Exception
	{
		icd10ClassExpanderImpl = new Icd10ClassExpanderImpl();
	}

	@DataProvider(name = "testExpandClassesProvider")
	public static Iterator<Object[]> testExpandClassesProvider() throws ParseException
	{
		Entity entityA = createEntity("A");
		Entity entityB = createEntity("B");
		Entity entityC = createEntity("C");
		Entity entityD = createEntity("D");
		Entity entityE = createEntity("E");
		Entity entityF = createEntity("F");
		Entity entityG = createEntity("G");
		when(entityA.getEntities("children")).thenReturn(asList(entityB, entityC));
		when(entityB.getEntities("children")).thenReturn(asList(entityD, entityE));
		when(entityC.getEntities("children")).thenReturn(asList(entityF, entityG));

		List<Object[]> dataList = new ArrayList<>();
		dataList.add(new Object[] { singleton(entityA),
				asList(entityA, entityB, entityC, entityD, entityE, entityF, entityG) });
		dataList.add(new Object[] { singleton(entityB), asList(entityB, entityD, entityE) });
		dataList.add(new Object[] { singleton(entityC), asList(entityC, entityF, entityG) });
		dataList.add(new Object[] { singleton(entityD), singleton(entityD) });
		dataList.add(new Object[] { singleton(entityE), singleton(entityE) });
		dataList.add(new Object[] { singleton(entityF), singleton(entityF) });
		dataList.add(new Object[] { singleton(entityG), singleton(entityG) });
		dataList.add(new Object[] { asList(entityA, entityB),
				asList(entityA, entityB, entityC, entityD, entityE, entityF, entityG) });
		dataList.add(new Object[] { asList(entityB, entityC),
				asList(entityB, entityC, entityD, entityE, entityF, entityG) });
		dataList.add(new Object[] { asList(entityD, entityE), asList(entityD, entityE) });
		dataList.add(new Object[] { asList(entityB, entityD), asList(entityB, entityD, entityE) });
		return dataList.iterator();
	}

	@Test(dataProvider = "testExpandClassesProvider")
	public void testExpandClasses(Collection<Entity> diseaseClasses, Collection<Entity> expectedExpandedDiseaseClasses)
	{
		Collection<Entity> expandedDiseaseClasses = icd10ClassExpanderImpl.expandClasses(diseaseClasses);
		assertEquals(Sets.newHashSet(expandedDiseaseClasses), Sets.newHashSet(expectedExpandedDiseaseClasses));
	}

	private static Entity createEntity(String idValue)
	{
		Entity entity = when(mock(Entity.class).getIdValue()).thenReturn(idValue).getMock();
		when(entity.getEntities("children")).thenReturn(emptyList());
		when(entity.toString()).thenReturn(idValue);
		return entity;
	}
}