package org.ovirt.engine.ui.uicommonweb.models.vms;

import org.ovirt.engine.ui.uicommonweb.models.ListModel;
import org.ovirt.engine.ui.uicommonweb.models.Model;

public class AttachCdModel extends Model {

    private ListModel<String> privateIsoImage;

    public ListModel<String> getIsoImage() {
        return privateIsoImage;
    }

    private void setIsoImage(ListModel<String> value) {
        privateIsoImage = value;
    }

    public AttachCdModel() {
        setIsoImage(new ListModel<String>());
    }
}
