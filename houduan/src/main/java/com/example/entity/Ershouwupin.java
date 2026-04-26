package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@TableName("ershouwupin")
@Data
public class Ershouwupin {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
	
	@TableField(value = "wupinbianhao")	private String wupinbianhao;	@TableField(value = "wupinmingcheng")	private String wupinmingcheng;	@TableField(value = "wupinleixing")	private String wupinleixing;	@TableField(value = "miaoshu")	private String miaoshu;	@TableField(value = "jiage")	private String jiage;	@TableField(value = "tupian")	private String tupian;	@TableField(value = "faburen")	private String faburen;	@TableField(value = "beizhu")	private String beizhu;	@TableField(value = "issh")	private Boolean issh;	
    
	
	
    @TableField(value = "addtime")
    private Date addtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getWupinbianhao() {
        return wupinbianhao;
    }
    public void setWupinbianhao(String wupinbianhao) {
		this.wupinbianhao = wupinbianhao;
    }	public String getWupinmingcheng() {
        return wupinmingcheng;
    }
    public void setWupinmingcheng(String wupinmingcheng) {
		this.wupinmingcheng = wupinmingcheng;
    }	public String getWupinleixing() {
        return wupinleixing;
    }
    public void setWupinleixing(String wupinleixing) {
		this.wupinleixing = wupinleixing;
    }	public String getMiaoshu() {
        return miaoshu;
    }
    public void setMiaoshu(String miaoshu) {
		this.miaoshu = miaoshu;
    }	public String getJiage() {
        return jiage;
    }
    public void setJiage(String jiage) {
		this.jiage = jiage;
    }	public String getTupian() {
        return tupian;
    }
    public void setTupian(String tupian) {
		this.tupian = tupian;
    }	public String getFaburen() {
        return faburen;
    }
    public void setFaburen(String faburen) {
		this.faburen = faburen;
    }	public String getBeizhu() {
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