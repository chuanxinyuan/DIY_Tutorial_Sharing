package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@TableName("dingdan")
@Data
public class Dingdan {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
	
	@TableField(value = "dingdanbianhao")
	private String dingdanbianhao;
	@TableField(value = "wupinbianhao")
	private String wupinbianhao;
	@TableField(value = "wupinmingcheng")
	private String wupinmingcheng;
	@TableField(value = "wupinleixing")
	private String wupinleixing;
	@TableField(value = "jiage")
	private String jiage;
	@TableField(value = "faburen")
	private String faburen;
	@TableField(value = "goumairen")
	private String goumairen;
	@TableField(value = "lianxidianhua")
	private String lianxidianhua;
	@TableField(value = "dizhi")
	private String dizhi;
	@TableField(value = "beizhu")
	private String beizhu;

    @TableField(value = "iszf")
    private String iszf;
    @TableField(value = "issh")
    private String issh;
    @TableField(value = "shhf")
    private String shhf;

    @TableField(value = "qiwangjiage")
    private String qiwangjiage;
	
	
    @TableField(value = "addtime")
    private Date addtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getDingdanbianhao() {
        return dingdanbianhao;
    }
    public void setDingdanbianhao(String dingdanbianhao) {
		this.dingdanbianhao = dingdanbianhao;
    }
	public String getWupinbianhao() {
        return wupinbianhao;
    }
    public void setWupinbianhao(String wupinbianhao) {
		this.wupinbianhao = wupinbianhao;
    }
	public String getWupinmingcheng() {
        return wupinmingcheng;
    }
    public void setWupinmingcheng(String wupinmingcheng) {
		this.wupinmingcheng = wupinmingcheng;
    }
	public String getWupinleixing() {
        return wupinleixing;
    }
    public void setWupinleixing(String wupinleixing) {
		this.wupinleixing = wupinleixing;
    }
	public String getJiage() {
        return jiage;
    }
    public void setJiage(String jiage) {
		this.jiage = jiage;
    }
	public String getFaburen() {
        return faburen;
    }
    public void setFaburen(String faburen) {
		this.faburen = faburen;
    }
	public String getGoumairen() {
        return goumairen;
    }
    public void setGoumairen(String goumairen) {
		this.goumairen = goumairen;
    }
	public String getLianxidianhua() {
        return lianxidianhua;
    }
    public void setLianxidianhua(String lianxidianhua) {
		this.lianxidianhua = lianxidianhua;
    }
	public String getDizhi() {
        return dizhi;
    }
    public void setDizhi(String dizhi) {
		this.dizhi = dizhi;
    }
	public String getBeizhu() {
        return beizhu;
    }
    public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
    }
	
	

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtme(Date addtime) {
        this.addtime = addtime;
    }
}