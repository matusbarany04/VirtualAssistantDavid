package com.david.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;
import com.sun.org.apache.xpath.internal.compiler.PsuedoNames;

import org.w3c.dom.Text;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;


public class DavidRenderer extends ApplicationAdapter
{
    /*SpriteBatch renderer;
    Texture david;

    Rectangle rect;
    float x;
    float y;

    @Override
    public void create()
    {
        renderer = new SpriteBatch();
        david = new Texture(Gdx.files.internal("david.png"));

        rect = new Rectangle
        (
            (Gdx.graphics.getWidth() / 2) - david.getWidth() * 2, (Gdx.graphics.getHeight() / 2) - david.getHeight() * 2, david.getWidth() * 2, david.getHeight() * 2
        );



    }

    @Override
    public void render ()
    {
        //Clearing the background
        Gdx.gl.glClearColor(0.5f, 0.5f, 1, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.begin();

        renderer.draw(
                david, rect.x, rect.y, rect.width, rect.height
                );

        renderer.end();


    }

    @Override
    public void dispose ()
    {
        renderer.dispose();
    } */


    private Environment environment;
    private PerspectiveCamera camera;
    private CameraInputController cameraController;
    private ModelBatch modelBatch;
    private Model model;
    private ModelInstance instance;

    private AnimationController animation;

    ArrayList<String> animations;

    public static Texture BGTex;
    public static Sprite BGsprite;
    public SpriteBatch SpriteRenderer;


    @Override
    public void create() {

        animations = new ArrayList<String>();
        animations.addAll(Arrays.asList("Idle.g3dj", "Ninja_Idle_1.g3dj", "Situps.g3dj", "Hanging_Idle.g3dj", "Swing_To_Land.g3dj"));

        environment = new Environment();
        environment.set(
                new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f)
        );
        environment.add(
                new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f)
        );

        modelBatch = new ModelBatch();


        camera = new PerspectiveCamera(
                67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
        );
        camera.position.set(10f, 150f, 150.0f);
        camera.lookAt(0, 150, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();


        int PseudoRandomIndex = (int)(Math.random() * animations.size());


        ModelBuilder modelBuilder = new ModelBuilder();
        model = new G3dModelLoader(
                new JsonReader()).loadModel(
                        Gdx.files.internal(animations.get(PseudoRandomIndex))
        );
        instance = new ModelInstance(model);

        cameraController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraController);

        animation = new AnimationController(instance);
        animation.animate("mixamo.com",  -1, 1f, null, 0.2f);

       for (Animation a : instance.animations)
       {
           System.out.println(a.id);
       }

       // 2D setup
       SpriteRenderer = new SpriteBatch();
       BGTex = new Texture(Gdx.files.internal("Forest.jpg"));
       BGsprite = new Sprite(BGTex);
    }

    @Override
    public void render() {
        cameraController.update();


        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();

        SpriteRenderer.begin();
        {
            BGsprite.draw(SpriteRenderer);
        }
        SpriteRenderer.end();

        modelBatch.begin(camera);
        {
            animation.update(delta);
            modelBatch.render(instance, environment);
        }
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
