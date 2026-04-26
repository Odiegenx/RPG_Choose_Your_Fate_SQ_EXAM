package dk.ek.gruppe2.chooseyourfate.dto;

public class UpdateEquipmentRequestDTO {

    private Integer headItemId;
    private Integer chestItemId;
    private Integer legsItemId;

    public Integer getHeadItemId() {
        return headItemId;
    }

    public void setHeadItemId(Integer headItemId) {
        this.headItemId = headItemId;
    }

    public Integer getChestItemId() {
        return chestItemId;
    }

    public void setChestItemId(Integer chestItemId) {
        this.chestItemId = chestItemId;
    }

    public Integer getLegsItemId() {
        return legsItemId;
    }

    public void setLegsItemId(Integer legsItemId) {
        this.legsItemId = legsItemId;
    }
}
