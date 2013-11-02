package com.htm.peopleresolution;

import com.htm.taskmodel.ILogicalPeopleGroupDef;

/**
 * Created with IntelliJ IDEA.
 * User: ungerts
 * Date: 01.11.13
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */
public interface ILPGResolutionProviderFactory {

    public IPeopleResolutionProvider createPeopleResolutionProvider(String lpgName);

}
